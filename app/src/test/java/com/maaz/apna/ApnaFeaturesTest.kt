package com.maaz.apna

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.test.core.app.ApplicationProvider
import com.maaz.apna.agent.DeviceActions
import com.maaz.apna.voice.ConversationControl
import com.maaz.apna.voice.VoiceInput
import com.maaz.apna.voice.WakePhrases
import com.maaz.apna.service.ApnaService
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ApnaFeaturesTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before fun reset() {
        context.getSharedPreferences("apna", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test fun sleepOnlyMatchesControlPhrases() {
        listOf("Go to sleep!", "Sleep", "Goodbye", "Apna so jao", "stand by").forEach {
            assertTrue(it, ConversationControl.isSleep(it))
        }
        listOf("Why do people sleep?", "Set a sleep timer", "Play Goodbye", "Call Sleep Clinic").forEach {
            assertFalse(it, ConversationControl.isSleep(it))
        }
    }

    @Test fun sleepClearsConversationWithoutStartingAModelTask() {
        shadowOf(ApplicationProvider.getApplicationContext<android.app.Application>())
            .grantPermissions(android.Manifest.permission.RECORD_AUDIO)
        val controller = Robolectric.buildService(ApnaService::class.java).create()
        try {
            val service = controller.get()
            val field = ApnaService::class.java.getDeclaredField("conversation").apply { isAccessible = true }
            @Suppress("UNCHECKED_CAST")
            val history = field.get(service) as ArrayDeque<Pair<String, String>>
            history.addLast("private question" to "private answer")
            service.onStartCommand(Intent(context, ApnaService::class.java)
                .setAction(ApnaService.ACTION_RUN).putExtra(ApnaService.EXTRA_TASK, "go to sleep"), 0, 1)
            assertTrue(history.isEmpty())
            assertNull(ApnaService.lastTask)
            assertNull(ApnaService.lastResult)
        } finally {
            controller.destroy()
        }
    }

    @Test fun timeoutIsBoundedAndPersistent() {
        val settings = Settings(context)
        assertEquals(15, settings.sleepTimeoutSeconds)
        settings.sleepTimeoutSeconds = 200
        assertEquals(60_000L, ConversationControl.timeoutMs(Settings(context).sleepTimeoutSeconds))
        settings.sleepTimeoutSeconds = -10
        assertEquals(5_000L, ConversationControl.timeoutMs(Settings(context).sleepTimeoutSeconds))
    }

    @Test fun phraseSelectionChangesActualDetectorTokens() {
        val settings = Settings(context)
        settings.wakePhraseId = "computer"
        val input = VoiceInput(context) {}
        assertEquals("Hey Computer", input.wakePhrase.label)
        assertTrue(input.wakePhrase.keywordLine().contains("▁COMP U TER"))
        assertFalse(WakePhrases.find("apna").keywordLine().contains("▁JA R VI S"))
        assertEquals("Open maps", VoiceInput.clean("hey computer open maps", "computer"))
        assertEquals("Set a timer", VoiceInput.clean("hey assistant set a timer", "assistant"))
        assertEquals("Explain computers", VoiceInput.clean("explain computers", "computer"))
    }

    @Test fun bundledSkillsAreActuallyLoaded() {
        val skills = Settings(context).skillInstructions()
        assertTrue(skills.contains("name: phone-tasks"))
        assertTrue(skills.contains("name: conversation"))
        assertTrue(skills.contains("draft is not a sent message"))
    }

    @Test fun phoneActionsRejectNonWebSchemesAndServiceCodes() {
        listOf("intent://launch", "file:///private", "javascript:alert(1)", "https://secret@example.com").forEach {
            assertThrows(IllegalArgumentException::class.java) { DeviceActions.validatedWebUri(it) }
        }
        assertEquals("https://example.com/path", DeviceActions.validatedWebUri("https://example.com/path").toString())
        assertThrows(IllegalArgumentException::class.java) { DeviceActions.phoneNumber("*123#") }
        assertEquals("+923001234567", DeviceActions.phoneNumber("+92 300 1234567"))
    }

    @Test fun smsActionOnlyOpensAnUnsentDraft() {
        val result = DeviceActions(context).execute(JSONObject()
            .put("action", "draft_sms").put("text", "+923001234567").put("body", "On my way"))
        val launched = shadowOf(ApplicationProvider.getApplicationContext<android.app.Application>()).nextStartedActivity
        assertEquals(Intent.ACTION_SENDTO, launched.action)
        assertEquals("smsto", launched.data?.scheme)
        assertEquals("+923001234567", launched.data?.schemeSpecificPart)
        assertEquals("On my way", launched.getStringExtra("sms_body"))
        assertTrue(result.contains("not been sent"))
    }

    @Test fun volumeActionHasValidatedBounds() {
        val actions = DeviceActions(context)
        assertThrows(IllegalArgumentException::class.java) {
            actions.execute(JSONObject().put("action", "volume").put("value", 101))
        }
        actions.execute(JSONObject().put("action", "volume").put("value", 0))
        assertEquals(0, context.getSystemService(AudioManager::class.java).getStreamVolume(AudioManager.STREAM_MUSIC))
    }

    @Test fun invalidAlarmCannotLaunchAnActivity() {
        assertThrows(IllegalArgumentException::class.java) {
            DeviceActions(context).execute(JSONObject().put("action", "alarm").put("hour", 25).put("minute", 0))
        }
    }
}
