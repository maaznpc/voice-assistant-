package com.maaz.apna.voice

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.content.pm.ServiceInfo
import android.os.Bundle
import android.os.Looper
import android.speech.RecognitionService
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.test.core.app.ApplicationProvider
import com.maaz.apna.ModelProfile
import com.maaz.apna.Settings
import com.maaz.apna.agent.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSpeechRecognizer
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MultilingualSpeechTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    @Before fun setup() {
        context.getSharedPreferences("apna", Context.MODE_PRIVATE).edit().clear().commit()
        val service = ResolveInfo().apply { serviceInfo = ServiceInfo().apply { name = "Speech"; packageName = "test.recognition" } }
        shadowOf(context.packageManager).addResolveInfoForIntent(Intent(RecognitionService.SERVICE_INTERFACE), service)
    }
    private fun result(text: String) = Bundle().apply { putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, arrayListOf(text)) }

    @Test fun selectedUrduServiceDeliversUrduAndMixedCommandsUnchanged() {
        Settings(context).speechLanguage = SpeechLanguage.ROMAN_URDU
        var heard: String? = null
        val listener = CommandListener(context)
        listener.listen { heard = it }
        shadowOf(Looper.getMainLooper()).idle()
        val service = shadowOf(ShadowSpeechRecognizer.getLatestSpeechRecognizer())
        assertEquals("ur-PK", service.lastRecognizerIntent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE))
        assertTrue(service.lastRecognizerIntent.getBooleanExtra(RecognizerIntent.EXTRA_ENABLE_LANGUAGE_DETECTION, false))
        service.triggerOnResults(result("WhatsApp کھولو اور علی کو میسج بھیجو"))
        assertEquals("WhatsApp کھولو اور علی کو میسج بھیجو", heard)
        assertTrue(service.isDestroyed)
    }

    @Test fun cancelledRecognitionCannotTriggerAnOldTask() {
        var deliveries = 0
        val listener = CommandListener(context)
        listener.listen { deliveries++ }
        shadowOf(Looper.getMainLooper()).idle()
        val old = shadowOf(ShadowSpeechRecognizer.getLatestSpeechRecognizer())
        listener.cancel()
        old.triggerOnResults(result("علی کو میسج بھیجو"))
        old.triggerOnError(SpeechRecognizer.ERROR_NETWORK)
        assertEquals(0, deliveries)
        assertNull(listener.lastError)
    }

    @Test fun silentFollowUpTimesOutOnceAndRejectsLateResults() {
        var deliveries = 0
        val listener = CommandListener(context)
        listener.listen(5000) { deliveries++; assertNull(it) }
        shadowOf(Looper.getMainLooper()).idle()
        val service = shadowOf(ShadowSpeechRecognizer.getLatestSpeechRecognizer())
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(5))
        service.triggerOnResults(result("late command"))
        assertEquals(1, deliveries)
        assertTrue(service.isDestroyed)
    }

    @Test fun nativeSleepIsLocalButOrdinaryUrduRequestsAreNotCancelled() {
        listOf("سو جاؤ!", "خدا حافظ", "Friday so jao", "सो जाओ").forEach { assertTrue(it, ConversationControl.isSleep(it)) }
        listOf("نیند کیوں آتی ہے؟", "مجھے سونے کا وقت بتاؤ", "Play Khuda Hafiz").forEach { assertFalse(it, ConversationControl.isSleep(it)) }
    }

    @Test fun installedUrduVoiceUsesNativeScriptInsteadOfAnEnglishVoice() {
        org.robolectric.shadows.ShadowTextToSpeech.addLanguageAvailability(java.util.Locale.forLanguageTag("ur-PK"))
        val voice = SystemVoice(context)
        voice.speak("میں فرائیڈے ہوں۔") {}
        val tts = org.robolectric.shadows.ShadowTextToSpeech.getLastTextToSpeechInstance()!!
        shadowOf(tts).onInitListener.onInit(android.speech.tts.TextToSpeech.SUCCESS)
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals("ur", shadowOf(tts).currentLanguage.language)
        assertEquals("میں فرائیڈے ہوں۔", shadowOf(tts).lastSpokenText)
        assertNull(voice.lastError)
        voice.shutdown()
    }

    @Test fun missingUrduVoiceReportsSetupProblemAndCompletesWithoutReadingItAsEnglish() {
        val voice = SystemVoice(context)
        var done = false
        voice.speak("میں فرائیڈے ہوں۔") { done = true }
        val tts = org.robolectric.shadows.ShadowTextToSpeech.getLastTextToSpeechInstance()!!
        shadowOf(tts).onInitListener.onInit(android.speech.tts.TextToSpeech.SUCCESS)
        shadowOf(Looper.getMainLooper()).idle()
        assertTrue(done)
        assertNull(shadowOf(tts).lastSpokenText)
        assertTrue(voice.lastError!!.contains("ur-PK"))
        voice.shutdown()
    }

    @Test fun romanDisplayAndUrduSpeechRemainSeparateThroughAgentResult() {
        val settings = Settings(context).apply {
            speechLanguage = SpeechLanguage.ROMAN_URDU
            saveProfile(ModelProfile(name = "Test", providerId = "custom", baseUrl = "http://localhost", apiKey = "", model = "mock"))
        }
        val host = object : AgentHost {
            override val isCancelled = false
            override fun status(text: String) = Unit
            override fun ask(question: String): String? = null
            override fun ensureUnlocked(): Boolean = error("Conversation needs no phone unlock")
        }
        val agent = PhoneAgent(settings, host) { _, prompt, tools ->
            assertTrue(prompt.contains("natural Roman Urdu"))
            assertTrue(tools.first { it.name == "finish" }.parameters.getJSONArray("required").toString().contains("spoken_summary"))
            object : ChatSession {
                override fun send(userText: String?, toolOutputs: List<ToolOutput>) = AssistantTurn("", listOf(ToolCall("done", "finish",
                    JSONObject().put("success", true).put("summary", "Ji, main Friday hoon.").put("spoken_summary", "جی، میں فرائیڈے ہوں۔"))))
            }
        }
        val answer = agent.run("Tumhara naam kya hai?")
        assertEquals("Ji, main Friday hoon.", answer.summary)
        assertEquals("جی، میں فرائیڈے ہوں۔", answer.spokenSummary)
        assertEquals("ur-PK", SpeechLanguage.ttsLocale(answer.spokenSummary))
    }
}
