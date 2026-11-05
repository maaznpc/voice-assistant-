package com.maaz.apna.voice

/** Bounded endpoint timing driven by neural VAD, with a conservative energy fallback. */
class SpeechTurnDetector(private val startMs: Long, waitMs: Long) {
    private val waitLimit = waitMs.coerceIn(1000, 60000)
    private var noise = 0.001f
    private var onsetFrames = 0
    private var speechAt = -1L
    private var lastVoiceAt = -1L
    var hasSpeech = false
        private set
    var finished = false
        private set
    fun accept(now: Long, rms: Float) {
        if (finished) return
        val threshold = maxOf(0.012f, noise * 3f)
        val voice = rms >= threshold
        if (!voice && !hasSpeech) noise = noise * .9f + rms.coerceIn(0f, .02f) * .1f
        acceptVoice(now, voice)
    }
    fun acceptVoice(now: Long, voice: Boolean) {
        if (finished) return
        if (!hasSpeech) {
            if (voice) onsetFrames++ else {
                onsetFrames = 0
            }
            if (onsetFrames >= 3) { hasSpeech = true; speechAt = now; lastVoiceAt = now }
            else if (now - startMs >= waitLimit) finished = true
        } else {
            if (voice) lastVoiceAt = now
            if (now - lastVoiceAt >= END_SILENCE_MS || now - speechAt >= 20_000) finished = true
        }
    }
    companion object { const val END_SILENCE_MS = 900L }
}
