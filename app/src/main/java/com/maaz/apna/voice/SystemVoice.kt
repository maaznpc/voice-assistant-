package com.maaz.apna.voice

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/** Android's built-in text-to-speech. Completion callbacks run on the main thread. */
class SystemVoice(private val context: Context) : VoiceEngine {
    private val main = Handler(Looper.getMainLooper())
    private val pending = ConcurrentHashMap<String, () -> Unit>()
    var lastError: String? = null
        private set
    private var initDone = false
    private var ready = false
    private val queued = mutableListOf<Pair<String, () -> Unit>>()

    private val tts: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        main.post {
            initDone = true
            ready = status == TextToSpeech.SUCCESS
            queued.forEach { (t, cb) -> if (ready) speak(t, cb) else cb() }
            queued.clear()
        }
    }

    init {
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onDone(utteranceId: String?) = finish(utteranceId)
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) = finish(utteranceId)
            override fun onError(utteranceId: String?, errorCode: Int) = finish(utteranceId)
        })
    }

    private fun finish(id: String?) {
        val cb = id?.let { pending.remove(it) } ?: return
        main.post(cb)
    }

    override fun speak(text: String, onDone: () -> Unit) {
        main.post {
            if (!initDone) { queued += text to onDone; return@post }
            if (!ready) { onDone(); return@post }
            val language = SpeechLanguage.ttsLocale(text)
            val result = tts.setLanguage(java.util.Locale.forLanguageTag(language))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                lastError = "No $language system voice. Install its TTS voice data in Android Settings or select a multilingual cloud voice. The reply is available on screen."
                android.widget.Toast.makeText(context, lastError, android.widget.Toast.LENGTH_LONG).show()
                onDone()
                return@post
            }
            lastError = null
            tts.setSpeechRate(com.maaz.apna.Settings(context).voiceSpeed)
            val id = UUID.randomUUID().toString()
            pending[id] = onDone
            if (tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, id) == TextToSpeech.ERROR) {
                lastError = "The system voice could not speak this reply."
                finish(id)
            }
        }
    }

    /** Stops speaking. Callbacks for the interrupted speech are dropped. */
    fun stop() {
        pending.clear()
        queued.clear()
        tts.stop()
    }

    override fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
