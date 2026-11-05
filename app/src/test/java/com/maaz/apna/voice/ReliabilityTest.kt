package com.maaz.apna.voice

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.maaz.apna.ApiEndpoints
import com.maaz.apna.ModelProfile
import com.maaz.apna.Settings
import com.maaz.apna.agent.OpenAiCompatibleSession
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReliabilityTest {
    @Test fun bundledNeuralModelMatchesThePinnedArtifact() {
        val ctx: Context = ApplicationProvider.getApplicationContext()
        val bytes = ctx.assets.open(LocalSpeechDetector.ASSET).use { it.readBytes() }
        val hash = java.security.MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
        assertEquals(LocalSpeechDetector.SHA256, hash)
    }

    @Test fun fullEndpointPastesKeepTheCustomPrefixWithoutDuplicatingPaths() {
        assertEquals("https://example.com/proxy/v1/chat/completions", ApiEndpoints.chat("https://example.com/proxy/v1/chat/completions/").toString())
        assertEquals("https://example.com/proxy/v1/audio/transcriptions", ApiEndpoints.audio("https://example.com/proxy/v1/audio/transcriptions").toString())
        assertThrows(IllegalArgumentException::class.java) { ApiEndpoints.chat("https://api.deepgram.com/v1") }
        assertFalse(ApiEndpoints.isDeepgram("https://api.deepgram.com.attacker.example/v1"))
    }

    @Test fun deepgramSpeechUsesTokenAuthenticationAndItsListenSchema() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("{\"results\":{\"channels\":[{\"alternatives\":[{\"transcript\":\"WhatsApp کھولو\"}]}]}}"))
            val profile = ModelProfile(name = "Deepgram", providerId = "deepgram", baseUrl = server.url("/v1").toString(), apiKey = "test-key", model = "whisper-large")
            val api = CloudTranscriber(OkHttpClient())
            val wav = ActiveAudioRecognizer.wav(byteArrayOf(1,2,3,4))
            api.newCall(profile, wav).execute().use { response -> assertEquals("WhatsApp کھولو", api.decode(profile, response.body!!.string())) }
            val request = server.takeRequest()
            assertEquals("/v1/listen", request.requestUrl!!.encodedPath)
            assertEquals("true", request.requestUrl!!.queryParameter("detect_language"))
            assertEquals("whisper-large", request.requestUrl!!.queryParameter("model"))
            assertEquals("Token test-key", request.getHeader("Authorization"))
            assertEquals("audio/wav", request.getHeader("Content-Type"))
            assertArrayEquals(wav, request.body.readByteArray())
        }
    }

    @Test fun oldDeepgramProfilesRemainIntactButCannotBecomeTheDialogueModel() {
        val ctx: Context = ApplicationProvider.getApplicationContext()
        ctx.getSharedPreferences("apna", Context.MODE_PRIVATE).edit().clear().commit()
        val s = Settings(ctx)
        val old = ModelProfile(name = "Old speech", providerId = "custom", baseUrl = "https://api.deepgram.com/v1", apiKey = "speech-key", model = "nova")
        s.saveProfile(old)
        assertNull(s.primary())
        assertTrue(s.modelsInOrder().isEmpty())
        assertEquals("speech-key", s.deepgramKey)
        assertEquals("deepgram", s.speechProfile()!!.providerId)
        val groq = ModelProfile(name = "Dialogue", providerId = "groq", baseUrl = "https://api.groq.com/openai/v1", apiKey = "dialogue-key", model = "openai/gpt-oss-20b")
        s.saveProfile(groq)
        assertEquals(groq.id, s.primary()!!.id)
        assertEquals(old, s.profiles().first { it.id == old.id })
        s.deepgramKey = ""
        assertEquals("groq", s.speechProfile()!!.providerId)
    }

    @Test fun microphoneFailureAndStalledInputCannotLookHealthy() {
        val health = MicrophoneHealth()
        health.starting(100)
        assertTrue(health.summary(200).contains("Opening"))
        assertTrue(health.summary(2200).contains("No microphone frames"))
        health.frame(2300, .006f)
        assertTrue(health.summary(2350).contains("receiving sound"))
        assertTrue(health.summary(4500).contains("stalled"))
        health.failed("Audio input failed")
        health.stopped()
        assertTrue(health.summary(5000).contains("error"))
        health.starting(6000)
        assertFalse(health.summary(6001).contains("error"))
    }

    @Test fun neuralSpeechEndsAfterNineHundredMillisecondsWithoutCuttingAShortPause() {
        val turn = SpeechTurnDetector(0, 5000)
        listOf(20L,40L,60L).forEach { turn.acceptVoice(it, true) }
        assertTrue(turn.hasSpeech)
        turn.acceptVoice(700, false)
        assertFalse(turn.finished)
        turn.acceptVoice(720, true)
        turn.acceptVoice(1600, false)
        assertFalse(turn.finished)
        turn.acceptVoice(1620, false)
        assertTrue(turn.finished)
    }

    @Test fun isolatedClickIsNotACommandAndSilenceIsBounded() {
        val turn = SpeechTurnDetector(0, 1000)
        turn.accept(20, .02f)
        turn.accept(40, 0f)
        turn.accept(1000, 0f)
        assertFalse(turn.hasSpeech)
        assertTrue(turn.finished)
    }

    @Test fun stoppingDuringTheWakeCueCannotStartLateRecognition() {
        val ctx: Context = ApplicationProvider.getApplicationContext()
        ctx.getSharedPreferences("apna", Context.MODE_PRIVATE).edit().clear().commit()
        val serviceInfo = android.content.pm.ResolveInfo().apply { this.serviceInfo = android.content.pm.ServiceInfo().apply { name = "Speech"; packageName = "test.recognition" } }
        org.robolectric.Shadows.shadowOf(ctx.packageManager).addResolveInfoForIntent(android.content.Intent(android.speech.RecognitionService.SERVICE_INTERFACE), serviceInfo)
        val controller = org.robolectric.Robolectric.buildService(com.maaz.apna.service.ApnaService::class.java).create()
        try {
            val service = controller.get()
            service.onStartCommand(android.content.Intent(ctx, com.maaz.apna.service.ApnaService::class.java).setAction(com.maaz.apna.service.ApnaService.ACTION_LISTEN), 0, 1)
            assertEquals(com.maaz.apna.service.ApnaService.Companion.Phase.LISTENING, com.maaz.apna.service.ApnaService.phase)
            service.onStartCommand(android.content.Intent(ctx, com.maaz.apna.service.ApnaService::class.java).setAction(com.maaz.apna.service.ApnaService.ACTION_STOP_TASK), 0, 2)
            org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(200))
            assertNull(org.robolectric.shadows.ShadowSpeechRecognizer.getLatestSpeechRecognizer())
        } finally { controller.destroy() }
    }

    @Test fun dialogueConnectionsAreReusedBetweenSessionsAndFullUrlIsNormalized() {
        MockWebServer().use { server ->
            repeat(2) { server.enqueue(MockResponse().setBody("{\"choices\":[{\"message\":{\"content\":\"OK\"},\"finish_reason\":\"stop\"}]}")) }
            val profile = ModelProfile(name = "Mock", providerId = "custom", baseUrl = server.url("/v1/chat/completions").toString(), apiKey = "test-key", model = "mock")
            repeat(2) { assertEquals("OK", OpenAiCompatibleSession(profile.toConfig(), "test", emptyList()).send("hello", emptyList()).text) }
            val first = server.takeRequest(); val second = server.takeRequest()
            assertEquals("/v1/chat/completions", first.path)
            assertEquals(0, first.sequenceNumber)
            assertEquals(1, second.sequenceNumber)
        }
    }
}
