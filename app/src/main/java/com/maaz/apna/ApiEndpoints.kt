package com.maaz.apna

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

/** Normalize pasted endpoint URLs without dropping a custom server's path prefix. */
object ApiEndpoints {
    fun isDeepgram(raw: String): Boolean = raw.toHttpUrlOrNull()?.host?.let { it == "deepgram.com" || it.endsWith(".deepgram.com") } == true
    fun speechOnly(profile: ModelProfile) = profile.providerId == "deepgram" || isDeepgram(profile.baseUrl)
    fun base(raw: String): HttpUrl {
        val parsed = raw.trim().toHttpUrlOrNull() ?: error("Enter a valid http:// or https:// API Base URL.")
        val path = parsed.encodedPath.trimEnd('/').removeSuffix("/chat/completions").removeSuffix("/audio/transcriptions").removeSuffix("/listen")
        return parsed.newBuilder().encodedPath(path.ifBlank { "/" }).query(null).fragment(null).build()
    }
    fun chat(raw: String): HttpUrl {
        require(!isDeepgram(raw)) { "Deepgram uses a speech/voice-agent API, not this chat-completions adapter. Put its key in Settings → Deepgram speech, then add a Groq/Gemini/OpenAI dialogue model." }
        return base(raw).newBuilder().addPathSegments("chat/completions").build()
    }
    fun audio(raw: String) = base(raw).newBuilder().addPathSegments("audio/transcriptions").build()
}
