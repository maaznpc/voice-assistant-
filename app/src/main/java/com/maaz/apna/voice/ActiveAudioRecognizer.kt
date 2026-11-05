package com.maaz.apna.voice

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import com.maaz.apna.ModelProfile
import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.sqrt

/** Short in-memory WAV after activation. No files, recordings, or uploads during standby. */
class ActiveAudioRecognizer(private val context: Context) {
    private val main = Handler(Looper.getMainLooper())
    private val worker = Executors.newSingleThreadExecutor { r -> Thread(r, "friday-active-audio").apply { isDaemon = true } }
    private val generation = AtomicInteger()
    private val transcriber = CloudTranscriber(OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(25, TimeUnit.SECONDS).callTimeout(25, TimeUnit.SECONDS).build())
    @Volatile private var recorder: AudioRecord? = null
    @Volatile private var call: Call? = null
    @Volatile var captureMs = 0L
        private set
    @Volatile var transcriptionMs = 0L
        private set

    @SuppressLint("MissingPermission") // The foreground service checks RECORD_AUDIO before activation.
    fun listen(profile: ModelProfile, timeoutMs: Long, result: (String?, String?) -> Unit) {
        cancel()
        val ticket = generation.get()
        captureMs = 0; transcriptionMs = 0
        worker.execute {
            val audio = ByteArrayOutputStream()
            var failure: String? = null
            var transcript: String? = null
            var speechDetector = LocalSpeechDetector.create(context)
            try {
                val size = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
                check(size > 0) { "Microphone format is unavailable." }
                val rec = AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, maxOf(size, 6400))
                recorder = rec
                try {
                    check(rec.state == AudioRecord.STATE_INITIALIZED) { "Microphone could not start." }
                    if (ticket != generation.get()) return@execute
                    rec.startRecording()
                    val start = android.os.SystemClock.elapsedRealtime()
                    val detector = SpeechTurnDetector(start, timeoutMs)
                    val buffer = ShortArray(320)
                    // A small pre-roll retains quiet consonants at speech onset.
                    val preRoll = ArrayDeque<ByteArray>()
                    while (ticket == generation.get()) {
                        val n = rec.read(buffer, 0, buffer.size)
                        check(n > 0) { "Microphone read failed." }
                        val now = android.os.SystemClock.elapsedRealtime()
                        var power = 0.0
                        val bytes = ByteBuffer.allocate(n * 2).order(ByteOrder.LITTLE_ENDIAN)
                        for (i in 0 until n) { power += buffer[i].toDouble() * buffer[i]; bytes.putShort(buffer[i]) }
                        val chunk = bytes.array()
                        val wasSpeaking = detector.hasSpeech
                        if (speechDetector != null) detector.acceptVoice(now, speechDetector.speech(buffer, n))
                        else detector.accept(now, (sqrt(power / n) / 32768.0).toFloat())
                        if (!wasSpeaking) {
                            preRoll.addLast(chunk)
                            while (preRoll.size > 15) preRoll.removeFirst()
                            if (detector.hasSpeech) { preRoll.forEach { audio.write(it) }; preRoll.clear() }
                        } else audio.write(chunk)
                        if (detector.finished) break

                    }
                    captureMs = android.os.SystemClock.elapsedRealtime() - start
                } finally {
                    runCatching { rec.stop() }; rec.release()
                    speechDetector?.release(); speechDetector = null
                    if (recorder === rec) recorder = null
                }
                if (ticket != generation.get()) return@execute
                if (audio.size() > 0) {
                    val request = transcriber.newCall(profile, wav(audio.toByteArray()))
                    call = request
                    if (ticket != generation.get()) { request.cancel(); return@execute }
                    val requestStart = android.os.SystemClock.elapsedRealtime()
                    request.execute().use { response ->
                        check(response.isSuccessful) { "Speech API HTTP ${response.code} from ${profile.name}. " + when (response.code) {
                            401, 403 -> "Check the speech API key and permissions."
                            404 -> "Check the speech endpoint and audio model; Deepgram must use /v1/listen."
                            429 -> "Speech credits or rate limit reached."
                            else -> "The speech service could not process this command."
                        } }
                        transcript = transcriber.decode(profile, response.body?.string().orEmpty()).ifBlank { null }
                    }
                    transcriptionMs = android.os.SystemClock.elapsedRealtime() - requestStart
                }
            } catch (e: Exception) {
                if (ticket != generation.get()) return@execute
                failure = e.message ?: "Automatic speech recognition failed."
            } finally { speechDetector?.release(); audio.reset() }
            main.post { if (ticket == generation.get()) result(transcript, failure) }
        }
    }

    fun cancel() {
        generation.incrementAndGet()
        call?.cancel(); call = null
        runCatching { recorder?.stop() }
    }
    fun shutdown() { cancel(); worker.shutdownNow() }

    companion object {
        internal fun wav(pcm: ByteArray): ByteArray = ByteBuffer.allocate(44 + pcm.size).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("RIFF".toByteArray()); putInt(36 + pcm.size); put("WAVEfmt ".toByteArray()); putInt(16)
            putShort(1); putShort(1); putInt(16000); putInt(32000); putShort(2); putShort(16)
            put("data".toByteArray()); putInt(pcm.size); put(pcm)
        }.array()
    }
}
