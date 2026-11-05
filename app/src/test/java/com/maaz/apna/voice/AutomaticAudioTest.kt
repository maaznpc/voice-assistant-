package com.maaz.apna.voice

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.maaz.apna.ModelProfile
import com.maaz.apna.Settings
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.nio.ByteBuffer
import java.nio.ByteOrder

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AutomaticAudioTest {
    @Test fun oldManualPreferencesCannotForceAReplyLanguage() {
        val ctx: Context = ApplicationProvider.getApplicationContext()
        for (id in listOf("english", "urdu", "roman_urdu", "hindi", "hinglish")) {
            ctx.getSharedPreferences("apna", Context.MODE_PRIVATE).edit().putString("speech_language", id).commit()
            assertEquals(SpeechLanguage.AUTO, Settings(ctx).speechLanguage)
            assertTrue(Settings(ctx).speechLanguage.prompt.contains("CURRENT request"))
        }
    }

    @Test fun androidAutoDetectionIncludesUrduHindiAndEnglish() {
        val intent = CommandListener.intent(SpeechLanguage.AUTO)
        assertEquals(listOf("ur-PK", "hi-IN", "en-US"), intent.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_SWITCH_ALLOWED_LANGUAGES))
    }

    @Test fun punctuationAloneDoesNotForceAnUrduVoiceForEnglishSpeech() {
        assertEquals("en-US", SpeechLanguage.ttsLocale("Hello، how are you؟"))
        assertEquals("ur-PK", SpeechLanguage.ttsLocale("جی، WhatsApp کھول رہا ہوں۔"))
        assertEquals("hi-IN", SpeechLanguage.ttsLocale("नमस्ते, how can I help?"))
    }

    @Test fun wavPreservesPcmAndDescribesTheActualAudioFormat() {
        val pcm = byteArrayOf(1, 2, 3, 4)
        val wav = ActiveAudioRecognizer.wav(pcm)
        val header = ByteBuffer.wrap(wav).order(ByteOrder.LITTLE_ENDIAN)
        assertEquals("RIFF", String(wav.copyOfRange(0, 4)))
        assertEquals(40, header.getInt(4))
        assertEquals(16000, header.getInt(24))
        assertEquals(4, header.getInt(40))
        assertArrayEquals(pcm, wav.copyOfRange(44, wav.size))
    }

    @Test fun whisperAutomaticallyTranscribesMixedSpeechWithoutForcingALanguage() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("{\"text\":\"WhatsApp کھولو\"}"))
            val profile = ModelProfile(name = "Groq", providerId = "groq", baseUrl = server.url("/openai/v1").toString(), apiKey = "test-key", model = "llm")
            val api = CloudTranscriber(OkHttpClient())
            api.newCall(profile, ActiveAudioRecognizer.wav(byteArrayOf(1, 2))).execute().use { response ->
                assertEquals("WhatsApp کھولو", api.decode(profile, response.body!!.string()))
            }
            val request = server.takeRequest()
            assertEquals("/openai/v1/audio/transcriptions", request.path)
            assertEquals("Bearer test-key", request.getHeader("Authorization"))
            val body = request.body.readUtf8()
            assertTrue(body.contains("whisper-large-v3-turbo"))
            assertFalse(body.contains("name=\"language\""))
        }
    }

    @Test fun geminiUsesTheConfiguredModelAndAudioKeyWithoutExecutingTheCommand() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"text\\\":\\\"mujhe AI samjhao\\\"}\"}]}}]}"))
            val profile = ModelProfile(name = "Gemini", providerId = "gemini", baseUrl = server.url("/v1beta/openai").toString(), apiKey = "test-key", model = "gemini-2.5-flash")
            val api = CloudTranscriber(OkHttpClient())
            api.newCall(profile, ActiveAudioRecognizer.wav(byteArrayOf(1, 2))).execute().use { response ->
                assertEquals("mujhe AI samjhao", api.decode(profile, response.body!!.string()))
            }
            val request = server.takeRequest()
            assertEquals("/v1beta/models/gemini-2.5-flash:generateContent", request.path)
            assertEquals("test-key", request.getHeader("x-goog-api-key"))
            val json = JSONObject(request.body.readUtf8())
            val parts = json.getJSONArray("contents").getJSONObject(0).getJSONArray("parts")
            assertTrue(parts.getJSONObject(0).getString("text").contains("Do not translate, answer or execute"))
            assertEquals("audio/wav", parts.getJSONObject(1).getJSONObject("inline_data").getString("mime_type"))
            assertFalse(json.has("tools"))
        }
    }
}
