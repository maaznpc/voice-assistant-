package com.maaz.apna.voice

import android.annotation.SuppressLint
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
class ActiveAudioRecognizer {
    private val main = Handler(Looper.getMainLooper())
    private val worker = Executors.newSingleThreadExecutor { r -> Thread(r, "friday-active-audio").apply { isDaemon = true } }
    private val generation = AtomicInteger()
    private val transcriber = CloudTranscriber(OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(25, TimeUnit.SECONDS).callTimeout(25, TimeUnit.SECONDS).build())
    @Volatile private var recorder: AudioRecord? = null
    @Volatile private var call: Call? = null

    @SuppressLint("MissingPermission") // The foreground service checks RECORD_AUDIO before activation.
    fun listen(profile: ModelProfile, timeoutMs: Long, result: (String?, String?) -> Unit) {
        cancel()
        val ticket = generation.get()
        worker.execute {
            val audio = ByteArrayOutputStream()
            var failure: String? = null
            var transcript: String? = null
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
                    var speechStart = 0L
                    var lastSpeech = 0L
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
                        val speech = sqrt(power / n) / 32768.0 >= 0.012
                        if (speechStart == 0L) {
                            preRoll.addLast(chunk)
                            while (preRoll.size > 15) preRoll.removeFirst()
                            if (speech) { speechStart = now; lastSpeech = now; preRoll.forEach { audio.write(it) }; preRoll.clear() }
                            else if (now - start >= timeoutMs.coerceIn(1000, 60000)) break
                        } else {
                            audio.write(chunk)
                            if (speech) lastSpeech = now
                            if (now - lastSpeech >= 1500 || now - speechStart >= 20000) break
                        }
                    }
                } finally {
                    runCatching { rec.stop() }; rec.release()
                    if (recorder === rec) recorder = null
                }
                if (ticket != generation.get()) return@execute
                if (audio.size() > 0) {
                    val request = transcriber.newCall(profile, wav(audio.toByteArray()))
                    call = request
                    if (ticket != generation.get()) { request.cancel(); return@execute }
                    request.execute().use { response ->
                        check(response.isSuccessful) { "Audio transcription failed (${response.code}). Check the primary provider's key, quota and audio-model access." }
                        transcript = transcriber.decode(profile, response.body?.string().orEmpty()).ifBlank { null }
                    }
                }
            } catch (e: Exception) {
                if (ticket != generation.get()) return@execute
                failure = e.message ?: "Automatic speech recognition failed."
            } finally { audio.reset() }
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
