package com.maaz.apna.voice

/** Audio-level diagnostics only; never stores recognized speech or a recording. */
class MicrophoneHealth {
    @Volatile private var active = false
    @Volatile private var openedAt = 0L
    @Volatile private var lastFrameAt = 0L
    @Volatile private var rawLevel = 0f
    @Volatile private var failure: String? = null
    @Volatile private var lastWakeAt = 0L

    fun starting(now: Long) { active = true; openedAt = now; lastFrameAt = 0; failure = null; rawLevel = 0f }
    fun frame(now: Long, rms: Float) { lastFrameAt = now; rawLevel = rms }
    fun detected(now: Long) { lastWakeAt = now }
    fun failed(message: String) { failure = message; active = false }
    fun stopped() { active = false; rawLevel = 0f }
    fun summary(now: Long): String {
        failure?.let { return "Microphone error: $it" }
        if (!active) return "Wake microphone idle"
        if (lastFrameAt == 0L) return if (now - openedAt > 2000) "No microphone frames received. Check Android microphone access and other recording apps." else "Opening wake microphone…"
        if (now - lastFrameAt > 2000) return "Wake microphone stalled; no fresh audio frames"
        val signal = when { rawLevel < 0.0001f -> "silent input—check Android mic privacy"; rawLevel < 0.003f -> "quiet input"; else -> "receiving sound" }
        val hit = if (lastWakeAt > 0) " · wake detected ${(now - lastWakeAt).coerceAtLeast(0) / 1000}s ago" else ""
        return "Wake mic: $signal · level ${String.format(java.util.Locale.US, "%.1f", rawLevel * 100)}%$hit"
    }
}
