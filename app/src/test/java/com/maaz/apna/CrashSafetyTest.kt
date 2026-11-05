package com.maaz.apna

import android.Manifest
import android.content.Context
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.maaz.apna.service.ApnaService
import com.maaz.apna.voice.Kokoro
import com.maaz.apna.voice.Speaker
import com.maaz.apna.voice.ModelDownloader
import com.maaz.apna.voice.SpeechModel
import com.maaz.apna.voice.WakeModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.File
import java.time.Duration

/**
 * The native speech libraries can fail to load on some phones (e.g. 16 KB memory pages). These
 * tests run where they *can't* load at all, and check Friday keeps running instead of crashing.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CrashSafetyTest {
    private val ctx: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        ctx.getSharedPreferences("apna", Context.MODE_PRIVATE).edit().clear().commit()
        shadowOf(ApplicationProvider.getApplicationContext<android.app.Application>()).grantPermissions(Manifest.permission.RECORD_AUDIO)
    }

    private fun fakeInstall(dir: File, names: Collection<String>) {
        dir.mkdirs(); names.forEach { File(dir, it).writeText("not a real model") }
    }

    private fun waitFor(timeoutMs: Long = 8000, cond: () -> Boolean): Boolean {
        val end = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < end) {
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(50))
            if (cond()) return true
            Thread.sleep(50)
        }
        return false
    }

    @Test
    fun activateSurvivesWakeWordEngineFailing() {
        fakeInstall(WakeModel.dir(ctx), WakeModel.KEEP_FILES)
        fakeInstall(SpeechModel.dir(ctx), SpeechModel.KEEP_FILES)
        assertTrue(WakeModel.isInstalled(ctx) && SpeechModel.isInstalled(ctx))
        val controller = Robolectric.buildService(ApnaService::class.java).create().startCommand(0, 1)
        assertTrue(ApnaService.running)
        assertTrue("reports the problem instead of crashing: ${ApnaService.status}",
            waitFor { ApnaService.status.contains("couldn't start") })
        controller.destroy()
        assertFalse(ApnaService.running)
    }

    @Test
    fun kokoroFailingFallsBackAndStillFinishesSpeaking() {
        Settings(ctx).voiceEngine = "kokoro"
        fakeInstall(Kokoro.modelDir(ctx), listOf("voices.bin", "model.int8.onnx", "tokens.txt"))
        val speaker = Speaker(ctx)
        assertTrue(speaker.usingKokoro)
        var done = false
        speaker.speak("Hello there.") { done = true }
        assertTrue("onDone still fires so the conversation can continue", waitFor { done })
        assertTrue(Kokoro.failed)
        assertFalse("switches to the system voice", speaker.usingKokoro)
    }

    @Test
    fun crashReportIsKeptAndCanBeDismissed() {
        CrashReporter.dismiss(ctx)
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        try {
            Thread.setDefaultUncaughtExceptionHandler { _, _ -> } // stand-in for the system handler
            CrashReporter.install(ApplicationProvider.getApplicationContext())
            Thread.getDefaultUncaughtExceptionHandler()!!.uncaughtException(Thread.currentThread(), IllegalStateException("boom"))
        } finally {
            Thread.setDefaultUncaughtExceptionHandler(previous)
        }
        val report = CrashReporter.pending(ctx)
        assertNotNull(report)
        assertTrue(report!!.contains("IllegalStateException: boom"))
        CrashReporter.dismiss(ctx)
        assertNull(CrashReporter.pending(ctx))
    }

    @Test
    fun wakeWordDownloadKeepsOnlySmallFiles() {
        val dir = WakeModel.dir(ctx)
        fakeInstall(dir, WakeModel.KEEP_FILES + listOf("encoder-epoch-12-avg-2-chunk-16-left-64.onnx", "bpe.model"))
        File(dir, "test_wavs").mkdirs(); File(dir, "test_wavs/0.wav").writeText("x")
        ModelDownloader.prune(dir, WakeModel.KEEP_FILES)
        assertEquals(WakeModel.KEEP_FILES, dir.list()!!.toSet())
    }
}
