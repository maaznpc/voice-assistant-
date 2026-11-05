package com.maaz.apna.voice

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.maaz.apna.Settings

/** System speech recognition only after activation; wake detection remains local. Main thread only. */
class CommandListener(private val context: Context) {
    private val main = Handler(Looper.getMainLooper())
    private var recognizer: SpeechRecognizer? = null
    private var generation = 0
    var lastError: String? = null
        private set

    fun listen(onResult: (String?) -> Unit) = listen(10_000L, onResult)

    fun listen(timeoutMs: Long, onResult: (String?) -> Unit) {
        cancel()
        lastError = null
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            lastError = "No speech recognition service. Install/enable a service supporting your selected language."
            onResult(null)
            return
        }
        val ticket = generation
        val r = SpeechRecognizer.createSpeechRecognizer(context)
        recognizer = r
        var delivered = false
        lateinit var timeout: Runnable
        fun deliver(text: String?) {
            if (delivered || ticket != generation) return
            delivered = true
            main.removeCallbacks(timeout)
            recognizer = null
            r.destroy()
            onResult(text)
        }
        timeout = Runnable { deliver(null) }
        r.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) = deliver(results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.trim()?.ifEmpty { null })
            override fun onError(error: Int) {
                if (ticket != generation || delivered) return
                lastError = when (error) {
                    12, 13 -> "Selected speech language is unavailable. Enable/download Urdu or Hindi in your phone's speech service."
                    SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Speech recognition needs a working internet connection."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
                    SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> null
                    else -> "Speech recognition failed ($error). Check your phone's speech service."
                }
                deliver(null)
            }
            override fun onReadyForSpeech(params: Bundle?) = Unit
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        })
        main.postDelayed(timeout, timeoutMs.coerceIn(1000L, 60_000L))
        try { r.startListening(intent(Settings(context).speechLanguage)) }
        catch (e: Exception) { lastError = "Speech recognition couldn't start: ${e.message}"; deliver(null) }
    }

    fun cancel() {
        generation++ // Invalidate callbacks before destroying the old recognizer.
        main.removeCallbacksAndMessages(null)
        val old = recognizer
        recognizer = null
        old?.destroy()
    }

    companion object {
        internal fun intent(language: SpeechLanguage): Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, language.locale)
            .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            .putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
            .apply {
                if (Build.VERSION.SDK_INT >= 34 && !language.isOffline) {
                    putExtra(RecognizerIntent.EXTRA_ENABLE_LANGUAGE_DETECTION, true)
                    putExtra(RecognizerIntent.EXTRA_ENABLE_LANGUAGE_SWITCH, RecognizerIntent.LANGUAGE_SWITCH_BALANCED)
                    putStringArrayListExtra(RecognizerIntent.EXTRA_LANGUAGE_DETECTION_ALLOWED_LANGUAGES, arrayListOf(language.locale, "en-US"))
                    putStringArrayListExtra(RecognizerIntent.EXTRA_LANGUAGE_SWITCH_ALLOWED_LANGUAGES, arrayListOf(language.locale, "en-US"))
                }
            }
    }
}
