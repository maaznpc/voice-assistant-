package com.maaz.apna.agent

import android.util.Base64
import com.maaz.apna.ProviderConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Any server that speaks the OpenAI Chat Completions API with tool calling:
 * OpenAI, LM Studio, Ollama, OpenRouter, Gemini, Groq, vLLM, llama.cpp server, ...
 */
class OpenAiCompatibleSession(
    private val config: ProviderConfig,
    system: String,
    toolSpecs: List<ToolSpec>,
) : ChatSession {

    // Reuse DNS/TLS connections across voice turns and model sessions.
    private val http = SHARED_HTTP.newBuilder()
        .readTimeout(if (config.preset.id in setOf("custom", "lmstudio", "ollama")) 240 else 45, TimeUnit.SECONDS)
        .build()

    private val tools = JSONArray().apply {
        toolSpecs.forEach { spec ->
            put(
                JSONObject().put("type", "function").put(
                    "function",
                    JSONObject().put("name", spec.name).put("description", spec.description).put("parameters", spec.parameters)
                )
            )
        }
    }

    private val messages = mutableListOf(JSONObject().put("role", "system").put("content", system))

    override fun send(userText: String?, toolOutputs: List<ToolOutput>): AssistantTurn {
        val images = mutableListOf<ByteArray>()
        for (out in toolOutputs) {
            messages += JSONObject().put("role", "tool").put("tool_call_id", out.callId)
                .put("content", if (out.isError) "ERROR: ${out.text}" else out.text)
            out.imageJpeg?.let { images += it }
        }
        if (userText != null || images.isNotEmpty()) {
            // Tool messages can't carry images, so screenshots go in a follow-up user message.
            val parts = JSONArray()
            parts.put(JSONObject().put("type", "text").put("text", userText ?: "Screenshot of the current screen:"))
            images.forEach { jpeg ->
                parts.put(
                    JSONObject().put("type", "image_url").put(
                        "image_url",
                        JSONObject().put("url", "data:image/jpeg;base64," + Base64.encodeToString(jpeg, Base64.NO_WRAP))
                    )
                )
            }
            messages += JSONObject().put("role", "user").put("content", if (images.isEmpty()) userText else parts)
        }

        val body = JSONObject()
            .put("model", config.model)
            .put("messages", JSONArray(trimmedHistory()))
            .put("tools", tools)
            .put("tool_choice", if (config.preset.id == "groq" && tools.length() > 0) "required" else "auto")

        val request = Request.Builder()
            .url(com.maaz.apna.ApiEndpoints.chat(config.baseUrl))
            .apply { if (config.apiKey.isNotBlank()) header("Authorization", "Bearer ${config.apiKey}") }
            .post(body.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val json = http.newCall(request).execute().use { resp ->
            val text = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) throw IOException("Dialogue API HTTP ${resp.code} from ${config.preset.label}. " +
                (if (resp.code == 404) "Check the Base URL and model ID; a pasted full chat endpoint is normalized. " else "") +
                (if (config.preset.id == "groq" && config.model.startsWith("llama-3.")) "Legacy Llama models are retired on Groq free/developer plans; try openai/gpt-oss-20b. " else "") + text.take(300))
            JSONObject(text)
        }

        val choice = json.getJSONArray("choices").getJSONObject(0)
        val message = choice.getJSONObject("message")
        val calls = mutableListOf<ToolCall>()
        val rawCalls = message.optJSONArray("tool_calls")
        if (rawCalls != null) {
            for (i in 0 until rawCalls.length()) {
                val c = rawCalls.getJSONObject(i)
                // Some local servers omit ids; the follow-up tool message needs one either way.
                if (c.optString("id").isBlank()) c.put("id", "call_" + UUID.randomUUID().toString().take(8))
                val fn = c.getJSONObject("function")
                val args = when (val a = fn.opt("arguments")) {
                    is JSONObject -> a
                    is String -> runCatching { JSONObject(a.ifBlank { "{}" }) }.getOrElse { JSONObject() }
                    else -> JSONObject()
                }
                calls += ToolCall(c.getString("id"), fn.getString("name"), args)
            }
        }
        messages += JSONObject().put("role", "assistant")
            .put("content", message.optString("content", "").takeUnless { message.isNull("content") } ?: "")
            .apply { if (rawCalls != null && rawCalls.length() > 0) put("tool_calls", rawCalls) }

        val finish = choice.optString("finish_reason")
        val note = if (finish == "length") "Response was cut off (max tokens)." else null
        return AssistantTurn(message.optString("content", "").takeUnless { message.isNull("content") } ?: "", calls, note)
    }

    /**
     * Screen dumps are large and only the latest one matters, so older ones are shortened before
     * sending. This keeps long tasks inside the small context windows local models often have.
     */
    private fun trimmedHistory(): List<JSONObject> {
        val observationIdx = messages.indices.filter { i ->
            val m = messages[i]
            m.optString("role") == "tool" || (m.optString("role") == "user" && m.opt("content") is JSONArray)
        }
        val keep = observationIdx.takeLast(KEEP_FULL_OBSERVATIONS).toSet()
        return messages.mapIndexed { i, m ->
            if (i !in observationIdx || i in keep) return@mapIndexed m
            val copy = JSONObject(m.toString())
            when (val c = copy.opt("content")) {
                is String -> copy.put("content", c.substringBefore(SCREEN_MARKER).trim() + "\n(older screen omitted)")
                is JSONArray -> copy.put("content", "(older screenshot omitted)")
            }
            copy
        }
    }

    companion object {
        private val SHARED_HTTP = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build()
        private const val KEEP_FULL_OBSERVATIONS = 3
        const val SCREEN_MARKER = "=== SCREEN"
    }
}
