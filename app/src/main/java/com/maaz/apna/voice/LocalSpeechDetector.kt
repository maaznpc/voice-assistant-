package com.maaz.apna.voice

import android.content.Context
import android.util.Log
import com.k2fsa.sherpa.onnx.SileroVadModelConfig
import com.k2fsa.sherpa.onnx.Vad
import com.k2fsa.sherpa.onnx.VadModelConfig

/** Tiny local neural VAD, loaded only during an activated command; no audio leaves this class. */
class LocalSpeechDetector private constructor(private val vad: Vad) {
    fun speech(pcm: ShortArray, count: Int): Boolean {
        vad.acceptWaveform(FloatArray(count) { pcm[it] / 32768f })
        val speech = vad.isSpeechDetected()
        while (!vad.empty()) vad.pop()
        return speech
    }
    fun release() = vad.release()
    companion object {
        const val ASSET = "models/silero_vad.onnx"
        const val SHA256 = "9e2449e1087496d8d4caba907f23e0bd3f78d91fa552479bb9c23ac09cbb1fd6"
        fun create(context: Context): LocalSpeechDetector? = try {
            LocalSpeechDetector(Vad(assetManager = context.assets, config = VadModelConfig(
                sileroVadModelConfig = SileroVadModelConfig(model = ASSET, threshold = .5f,
                    minSilenceDuration = .06f, minSpeechDuration = .06f, windowSize = 512, maxSpeechDuration = 20f),
                sampleRate = 16000, numThreads = 1,
            )))
        } catch (e: Throwable) {
            Log.w("LocalSpeechDetector", "Neural VAD unavailable; using bounded energy endpointing", e)
            null
        }
    }
}
