package com.maaz.apna.voice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CleanTranscriptTest {
    private fun c(s: String) = VoiceInput.clean(s)

    @Test fun stripsWakeWordVariants() {
        assertEquals("Send my wife a message", c("hey friday send my wife a message"))
        assertEquals("Turn on the flashlight", c("Hey Fri day turn on the flashlight"))
        assertEquals("Open YouTube", c("Friday, open YouTube"))
        assertEquals("Call mum", c("okay hey Friday call mum"))
        assertEquals("", c("Hey Friday."))
    }

    @Test fun stripsOwnPrompt() {
        assertEquals("Open settings", c("Friday yes? open settings"))
        assertEquals("Open settings", c("yes, open settings"))
    }

    @Test fun leavesRealCommandsAlone() {
        assertEquals("Text Travis that I'm late", c("text Travis that I'm late"))
        assertEquals("Turn on the flashlight", c("turn on the flashlight"))
        assertEquals("Play the harvest playlist", c("play the harvest playlist"))
        assertEquals("On Friday call mum", c("on Friday call mum"))
        assertEquals("Next Friday set an alarm", c("next Friday set an alarm"))
        assertEquals("Is Friday a holiday?", c("is Friday a holiday?"))
    }

    private val reply = VoiceInput.words("Done. I sent your wife a message on WhatsApp saying I love you.").toSet()

    @Test
    fun apnasOwnEchoIsNotAnInterruption() {
        // What the recogniser makes of Friday's own voice, including small mishearings.
        assertFalse(VoiceInput.isBargeIn("DONE I SENT YOUR WIFE A MESSAGE", reply))
        assertFalse(VoiceInput.isBargeIn("SENT YOUR WIVE A MESSAGE ON WHATSAP", reply))
        assertFalse(VoiceInput.isBargeIn("", reply))
    }

    @Test
    fun talkingOverApnaIsAnInterruption() {
        assertTrue(VoiceInput.isBargeIn("NO SEND IT TO MY BROTHER", reply))
        assertTrue("one stop word is enough", VoiceInput.isBargeIn("STOP", reply))
        assertTrue(VoiceInput.isBargeIn("I SENT YOUR WIFE WAIT", reply))
    }

    @Test
    fun echoAtTheStartOfAnInterruptionIsDropped() {
        assertEquals("No send it to my brother", VoiceInput.stripEcho("message on WhatsApp No send it to my brother", reply))
        assertEquals("Call mum", VoiceInput.stripEcho("Call mum", reply))
        assertEquals("Call mum", VoiceInput.stripEcho("Call mum", null))
    }

    @Test
    fun stopPhrasesAreRecognised() {
        listOf("Stop", "stop it", "Never mind", "Shut up.", "Okay, stop", "that's enough").forEach {
            assertTrue(it, VoiceInput.isStopCommand(it))
        }
        listOf("Stop the music", "Cancel my alarm", "Wait for me").forEach { assertFalse(it, VoiceInput.isStopCommand(it)) }
    }

    @Test
    fun partsOfApnasOwnWordsAreEcho() {
        val spoken = VoiceInput.words("I set a stopwatch for ten minutes on WhatsApp.").toSet()
        assertFalse("'stop' is the start of 'stopwatch'", VoiceInput.isBargeIn("I SET A STOP", spoken))
        assertFalse(VoiceInput.isBargeIn("WHAT'S APP", spoken))
    }

    @Test
    fun stopKeywordIsOffWhenApnaSaysSomethingLikeIt() {
        assertTrue(VoiceInput.soundsLikeStop("The top story is about the stock market."))
        assertTrue(VoiceInput.soundsLikeStop("Your next stop is Oxford Street."))
        assertTrue(VoiceInput.soundsLikeStop("Let me start the timer."))
        assertFalse(VoiceInput.soundsLikeStop("Here is what I found on the weather today."))
        assertFalse(VoiceInput.soundsLikeStop("Done. I sent your wife a message on WhatsApp."))
    }
}
