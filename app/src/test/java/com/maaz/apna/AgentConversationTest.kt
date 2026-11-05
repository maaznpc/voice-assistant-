package com.maaz.apna

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.maaz.apna.agent.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AgentConversationTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var settings: Settings
    private val host = object : AgentHost {
        override val isCancelled = false
        override fun status(text: String) = Unit
        override fun ask(question: String) = "Yes"
        override fun ensureUnlocked(): Boolean = error("Conversation must not request an unlock")
    }

    @Before fun setup() {
        context.getSharedPreferences("apna", Context.MODE_PRIVATE).edit().clear().commit()
        settings = Settings(context)
        settings.saveProfile(ModelProfile(name = "Test", providerId = "custom", baseUrl = "http://localhost", apiKey = "", model = "mock"))
    }

    @Test fun ordinaryConversationWorksWithoutAccessibilityOrUnlock() {
        val session = object : ChatSession {
            override fun send(userText: String?, toolOutputs: List<ToolOutput>) = AssistantTurn("", listOf(
                ToolCall("done", "finish", JSONObject().put("success", true).put("summary", "Hello, I am Friday."))))
        }
        val result = PhoneAgent(settings, host) { _, _, _ -> session }.run("Hello")
        assertTrue(result.success)
        assertEquals("Hello, I am Friday.", result.summary)
    }

    @Test fun subsequentCallsInOneTurnCannotActOnAnOldScreen() {
        var step = 0
        val session = object : ChatSession {
            override fun send(userText: String?, toolOutputs: List<ToolOutput>): AssistantTurn {
                if (step++ == 0) return AssistantTurn("", listOf(
                    ToolCall("first", "device_action", JSONObject().put("action", "battery")),
                    ToolCall("stale", "open_app", JSONObject().put("name", "Settings")),
                ))
                assertEquals(2, toolOutputs.size)
                assertFalse(toolOutputs[0].isError)
                assertTrue(toolOutputs[1].isError)
                assertTrue(toolOutputs[1].text.startsWith("Not executed"))
                return AssistantTurn("", listOf(ToolCall("done", "finish",
                    JSONObject().put("success", true).put("summary", "Checked battery."))))
            }
        }
        assertTrue(PhoneAgent(settings, host) { _, _, _ -> session }.run("Check battery").success)
        assertEquals(2, step)
    }
}
