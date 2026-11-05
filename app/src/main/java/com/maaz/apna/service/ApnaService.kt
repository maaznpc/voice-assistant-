package com.maaz.apna.service

import android.Manifest
import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.maaz.apna.R
import com.maaz.apna.accessibility.ApnaAccessibilityService
import com.maaz.apna.Settings
import com.maaz.apna.agent.AgentHost
import com.maaz.apna.agent.PhoneAgent
import com.maaz.apna.ui.MainActivity
import com.maaz.apna.voice.ConversationControl
import com.maaz.apna.voice.CommandListener
import com.maaz.apna.voice.Speaker
import com.maaz.apna.voice.VoiceInput
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Long-running foreground service: waits for "Hey Friday", listens for the command, runs the
 * phone agent on a worker thread, and speaks the result. Voice work happens on the main thread.
 */
class ApnaService : Service() {

    private enum class State { IDLE, LISTENING_COMMAND, WORKING }

    private val main = Handler(Looper.getMainLooper())
    private val worker = Executors.newSingleThreadExecutor()
    private lateinit var settings: Settings
    private lateinit var speaker: Speaker
    private lateinit var voice: VoiceInput
    /** Fallback when the offline speech model isn't downloaded. */
    private lateinit var google: CommandListener
    @Volatile private var state = State.IDLE
    @Volatile private var cancelled = false
    private var startFailed = false
    /** Set in onDestroy; stops late callbacks (speech finishing, models loading) from listening again. */
    @Volatile private var destroyed = false
    private var listeningGeneration = 0
    private var listenerWakeLock: PowerManager.WakeLock? = null
    private val renewWakeLock = object : Runnable {
        override fun run() {
            if (destroyed || startFailed) return
            if (state == State.IDLE && (!settings.wakeWordEnabled || !voice.hasWakeWord)) {
                listenerWakeLock?.let { if (it.isHeld) it.release() }
            } else {
                val lock = listenerWakeLock ?: getSystemService(PowerManager::class.java)
                    .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "apna:listener")
                    .apply { setReferenceCounted(false) }.also { listenerWakeLock = it }
                lock.acquire(15 * 60_000L)
            }
            main.postDelayed(this, 10 * 60_000L)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        // Models from earlier versions that are no longer used.
        listOf("vosk-model", "kws.old").forEach { java.io.File(filesDir, it).takeIf { f -> f.exists() }?.deleteRecursively() }
        settings = Settings(this)
        speaker = Speaker(this)
        voice = VoiceInput(this) { onWakeWord() }
        google = CommandListener(this)
        createChannel()
        try {
            ServiceCompat.startForeground(
                this, NOTIFICATION_ID, notification("Starting…"),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE else 0
            )
        } catch (e: Exception) {
            // Android refuses microphone services without the permission, or when started from the background.
            Log.w(TAG, "could not start in the foreground", e)
            startFailed = true
            stopSelf()
            return
        }
        running = true
        renewWakeLock.run()
        resumeListening()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (startFailed) return START_NOT_STICKY
        when (intent?.action) {
            ACTION_REFRESH -> {
                voice.wakeEnabled = settings.wakeWordEnabled
                main.removeCallbacks(renewWakeLock)
                renewWakeLock.run()
                if (state == State.IDLE) resumeListening()
            }
            ACTION_STOP_TASK -> {
                cancelled = true
                voice.cancelCommand(); google.cancel()
                if (state == State.LISTENING_COMMAND) {
                    // Cancel listening: nothing will call onHeard now, so go back to waiting.
                    stopSpeaking()
                    state = State.IDLE
                    island { it.hide() }
                    update("Cancelled")
                    resumeListening()
                } else {
                    stopSpeaking()
                    if (state == State.WORKING) update("Stopping…")
                }
            }
            ACTION_SHUTDOWN -> {
                settings.switchedOn = false
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_RUN -> intent.getStringExtra(EXTRA_TASK)?.let { runTask(it) }
            ACTION_LISTEN -> if (state == State.IDLE) onWakeWord()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        destroyed = true
        listeningGeneration++
        main.removeCallbacks(renewWakeLock)
        listenerWakeLock?.let { if (it.isHeld) it.release() }
        running = false
        phase = Phase.OFF
        status = "Offline"
        statusListener?.invoke(status)
        cancelled = true
        island { it.hide() }
        if (!startFailed) {
            voice.release()
            google.cancel()
            speaker.shutdown()
        }
        worker.shutdownNow()
        super.onDestroy()
    }

    // ---- Voice flow ----
    //
    // A conversation: "Hey Friday" (or a follow-up straight after a reply) → listen → work → reply.
    // The microphone stays open while Friday works and while it replies, so the user can interrupt
    // at any point: by saying "Hey Friday" or, during a reply, simply by talking over it.

    /** What to do when the current speech ends naturally (also run if it's stopped from the island). */
    private var afterSpeech: (() -> Unit)? = null
    private var speechId = 0
    /** Increases with every task; a result from a task that was interrupted is never spoken. */
    @Volatile private var taskId = 0
    /** Recent requests and replies, so follow-ups like "and tell her I'll be late" make sense. */
    private val conversation = ArrayDeque<Pair<String, String>>()
    private var lastExchangeAt = 0L

    /**
     * Speaks [text]. If [interruptible] (and the user allows it), the microphone stays open and
     * talking over Friday stops it and becomes the next command; otherwise the microphone is muted
     * so Friday never hears itself.
     */
    private fun say(text: String, interruptible: Boolean = false, then: () -> Unit = {}) {
        val id = ++speechId
        val bargeIn = interruptible && settings.interruptByTalking && voice.canTranscribe && voice.isListening
        if (bargeIn) voice.listenWhileSpeaking(text) { stop -> onBargeIn(stop) } else voice.muted = true
        afterSpeech = {
            voice.stopListeningWhileSpeaking()
            // After muted speech, let the speaker's echo die away before listening again.
            main.postDelayed({
                if (!destroyed && id == speechId) { voice.muted = false; then() }
            }, if (bargeIn) 0L else 150L)
        }
        island { it.speaking(text) { speaker.level() } }
        speaker.speak(text) { if (id == speechId) finishSpeech() }
    }

    private fun finishSpeech() {
        val then = afterSpeech ?: return
        afterSpeech = null
        island { if (it.isShowing) it.speakingDone() }
        then()
    }

    /** Stops talking now, carrying on as if the speech had finished. */
    private fun stopSpeaking() {
        speechId++
        speaker.stop()
        if (afterSpeech != null) finishSpeech() else island { if (it.isShowing) it.speakingDone() }
    }

    /** Stops talking now and forgets what was going to happen next (the user has taken over). */
    private fun interruptSpeech() {
        speechId++
        afterSpeech = null
        voice.stopListeningWhileSpeaking()
        voice.muted = false
        speaker.stop()
        island { if (it.isShowing) it.speakingDone() }
    }

    private fun onWakeWord() {
        if (destroyed) return
        when (state) {
            State.LISTENING_COMMAND -> return
            State.WORKING -> {
                // "Hey Friday" during a task: stop it and take the new command.
                cancelled = true
                taskId++
                interruptSpeech()
                listen(promptIfQuiet = true)
            }
            State.IDLE -> {
                interruptSpeech()
                listen(promptIfQuiet = true)
            }
        }
    }

    /** The user talked over Friday: what they're saying is the next command, or they said "stop". */
    private fun onBargeIn(stop: Boolean) {
        if (destroyed) return
        interruptSpeech()
        if (state == State.WORKING) { cancelled = true; taskId++ }
        if (stop) {
            state = State.IDLE
            voice.cancelCommand()
            island { it.hide() }
            update("Okay")
            resumeListening()
        } else {
            listen(promptIfQuiet = false, interrupted = true)
        }
    }

    /**
     * Transcribes the next thing the user says. After "Hey Friday" ([promptIfQuiet]) Friday says
     * "Yes?" if they pause; for a [followUp] it waits quietly for a few seconds and gives up silently.
     */
    private fun listen(promptIfQuiet: Boolean, followUp: Boolean = false, interrupted: Boolean = false) {
        val generation = ++listeningGeneration
        state = State.LISTENING_COMMAND
        main.removeCallbacks(renewWakeLock)
        renewWakeLock.run()
        update("Listening…")
        speaker.warmUp() // load the voice now, so the reply has no delay
        island { it.listening(if (voice.canTranscribe) ({ voice.partialText }) else null, { voice.level }) }
        if (voice.canTranscribe) {
            voice.start()
            // Keeps transcribing from the wake word on, so "Hey Friday, call mum" works in one breath.
            voice.listenForCommand(if (followUp) ConversationControl.timeoutMs(settings.sleepTimeoutSeconds) else 7000) { text ->
                if (!destroyed && generation == listeningGeneration) onHeard(text, followUp, interrupted)
            }
            if (promptIfQuiet) main.postDelayed({
                // Not muted: if the user starts talking over it, nothing is lost (a stray "yes" is stripped).
                if (state == State.LISTENING_COMMAND && !voice.speechSinceWake && voice.partialText.isEmpty()) {
                    island { it.speaking("Yes?") { speaker.level() } }
                    speaker.speak("Yes?") { island { if (it.isShowing) it.speakingDone() } }
                }
            }, 700)
        } else {
            voice.stop() // Google's recognizer needs the microphone
            val heard: (String?) -> Unit = { text ->
                if (!destroyed && generation == listeningGeneration) onHeard(text, followUp)
            }
            if (followUp) google.listen(heard) else say("Yes?") { google.listen(heard) }
        }
    }

    private fun onHeard(text: String?, followUp: Boolean = false, interrupted: Boolean = false) {
        if (state != State.LISTENING_COMMAND) return
        when {
            // A one-word interruption is almost always "stop", "wait" or "no", often half drowned out
            // by Friday's voice. Don't act on it; just listen for what comes next.
            interrupted && text != null && VoiceInput.words(text).size <= 1 && !VoiceInput.isStopCommand(text) ->
                listen(promptIfQuiet = false, followUp = true)
            text.isNullOrBlank() && followUp -> {
                enterStandby()
            }
            text.isNullOrBlank() -> {
                state = State.IDLE
                update("Didn't catch that")
                say("Sorry, I didn't catch that.", interruptible = true) { island { it.hide() }; resumeListening() }
            }
            ConversationControl.isSleep(text) -> enterStandby()
            VoiceInput.isStopCommand(text) -> {
                state = State.IDLE
                island { it.hide() }
                update("Okay")
                resumeListening()
            }
            else -> runTask(text)
        }
    }

    private fun enterStandby() {
        listeningGeneration++
        cancelled = true
        taskId++
        interruptSpeech()
        voice.cancelCommand()
        google.cancel()
        conversation.clear()
        lastTask = null
        lastResult = null
        state = State.IDLE
        island { it.hide() }
        resumeListening()
    }

    private fun runTask(task: String) {
        if (ConversationControl.isSleep(task)) { enterStandby(); return }
        if (destroyed || state == State.WORKING) return
        listeningGeneration++
        state = State.WORKING
        main.removeCallbacks(renewWakeLock)
        renewWakeLock.run()
        cancelled = false
        val id = ++taskId
        voice.cancelCommand()
        // Keep listening for "Hey Friday" while working, so the user can stop or redirect the task.
        if (voice.hasWakeWord && settings.wakeWordEnabled) voice.start()
        lastTask = task
        island { it.working(task) }
        update("Working on: $task")
        // Start thinking immediately; unnecessary acknowledgement adds delay.
        val earlier = recentConversation()
        worker.execute {
            val result = try {
                PhoneAgent(settings, hostFor(id)).run(task, earlier)
            } catch (e: Throwable) {
                Log.e(TAG, "task failed", e)
                com.maaz.apna.agent.AgentResult(false, "Something went wrong: ${e.message}")
            }
            main.post {
                if (id != taskId || destroyed) return@post // interrupted by a newer command
                lastResult = result.summary
                remember(task, result.summary)
                island { it.finish(result.success, result.summary) }
                update(result.summary)
                say(result.summary, interruptible = true) {
                    state = State.IDLE
                    afterReply()
                }
            }
        }
    }

    /** After a reply: listen for a follow-up without the wake word, or go back to waiting. */
    private fun afterReply() {
        if (destroyed) return
        if (settings.followUp && voice.canTranscribe) listen(promptIfQuiet = false, followUp = true)
        else enterStandby()
    }

    private fun remember(task: String, reply: String) {
        conversation.addLast(task to reply)
        while (conversation.size > 3) conversation.removeFirst()
        lastExchangeAt = System.currentTimeMillis()
    }

    /** The last few exchanges, if the conversation is still going (within 10 minutes). */
    private fun recentConversation(): List<Pair<String, String>> {
        if (System.currentTimeMillis() - lastExchangeAt > 10 * 60_000) conversation.clear()
        return conversation.toList()
    }

    /** Goes back to waiting for "Hey Friday" (loading any models downloaded since). */
    private fun resumeListening() {
        if (destroyed || state != State.IDLE) return
        voice.echoCancellation = settings.interruptByTalking
        voice.load { error ->
            if (destroyed || state != State.IDLE) return@load
            voice.wakeEnabled = settings.wakeWordEnabled
            when {
                error != null -> update(error)
                !settings.wakeWordEnabled -> { voice.stop(); update("Ready. Tap the mic to talk.") }
                !voice.hasWakeWord -> update("Ready. Download the wake word in Settings to use \"Hey Friday\".")
                else -> { voice.start(); update("Say \"${voice.wakePhrase.label}\"") }
            }
            main.removeCallbacks(renewWakeLock)
            renewWakeLock.run()
        }
    }

    /** Accessibility services can't act behind the lock screen, so wake the screen and wait for an unlock. */
    @Suppress("DEPRECATION")
    private fun waitForUnlock(shouldCancel: () -> Boolean) {
        val keyguard = getSystemService(KeyguardManager::class.java)
        if (!keyguard.isKeyguardLocked) return
        val pm = getSystemService(PowerManager::class.java)
        pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "apna:wake")
            .acquire(3000)
        speakAndWait("Please unlock your phone and I'll get started.", shouldCancel)
        val deadline = System.currentTimeMillis() + 30_000
        while (keyguard.isKeyguardLocked && System.currentTimeMillis() < deadline && !shouldCancel()) Thread.sleep(300)
        check(!keyguard.isKeyguardLocked) { "Unlock your phone before I can control apps." }
    }

    /** For the worker thread: speak and block until done. */
    private fun speakAndWait(text: String, shouldCancel: () -> Boolean) {
        val done = CountDownLatch(1)
        main.post { if (!shouldCancel() && !destroyed) say(text) { done.countDown() } else done.countDown() }
        val deadline = System.currentTimeMillis() + 30_000
        while (!done.await(200, TimeUnit.MILLISECONDS) && !shouldCancel() && System.currentTimeMillis() < deadline) { /* cancellable wait */ }
    }

    /** Bridges the agent (worker thread) to the voice UI (main thread) for task [id]. */
    private fun hostFor(id: Int) = object : AgentHost {
        override val isCancelled get() = cancelled || id != taskId
        override fun ensureUnlocked(): Boolean {
            if (isCancelled) return false
            waitForUnlock { isCancelled }
            return !isCancelled
        }
        override fun status(text: String) { main.post { if (!isCancelled && !destroyed) update(text) } }
        override fun ask(question: String): String? {
            val latch = CountDownLatch(1)
            var answer: String? = null
            main.post {
                if (isCancelled || destroyed) { latch.countDown(); return@post }
                update("Asking: $question")
                say(question) {
                    island { it.listening(if (voice.canTranscribe) ({ voice.partialText }) else null, { voice.level }) }
                    val got: (String?) -> Unit = { text ->
                        if (!isCancelled && !destroyed) { answer = text; island { it.working() } }
                        latch.countDown()
                    }
                    if (voice.canTranscribe) voice.listenForCommand(10_000) { got(it) }
                    else { voice.stop(); google.listen { got(it); if (voice.hasWakeWord) voice.start() } }
                }
            }
            // Give up after a minute, or straight away if the task is stopped.
            val deadline = System.currentTimeMillis() + 60_000
            while (!latch.await(200, TimeUnit.MILLISECONDS)) {
                if (isCancelled || System.currentTimeMillis() > deadline) break
            }
            return if (isCancelled) null else answer
        }
    }

    // ---- Notification ----

    /** Runs [block] on the task island, if phone control is on and the island is enabled. */
    private fun island(block: (com.maaz.apna.ui.TaskIsland) -> Unit) {
        if (!settings.showIsland) return
        ApnaAccessibilityService.instance?.island?.let {
            it.onStop = { start(this, ACTION_STOP_TASK) }
            runCatching { block(it) }
        }
    }

    private fun update(text: String) {
        status = text
        if (state == State.WORKING && !text.startsWith("Working on:")) island { it.status(text) }
        phase = when (state) {
            State.IDLE -> Phase.IDLE
            State.LISTENING_COMMAND -> Phase.LISTENING
            State.WORKING -> Phase.WORKING
        }
        getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification(text))
        statusListener?.invoke(text)
    }

    private fun createChannel() {
        val channel = NotificationChannel(CHANNEL, "Friday", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun notification(text: String): Notification {
        val open = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        fun action(action: String, code: Int) =
            PendingIntent.getService(this, code, Intent(this, ApnaService::class.java).setAction(action), PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_stat_apna)
            .setContentTitle("Friday")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(open)
            .addAction(0, "Talk", action(ACTION_LISTEN, 1))
            .addAction(0, "Stop task", action(ACTION_STOP_TASK, 2))
            .addAction(0, "Turn off", action(ACTION_SHUTDOWN, 3))
            .build()
    }

    companion object {
        private const val TAG = "ApnaService"
        private const val CHANNEL = "apna"
        private const val NOTIFICATION_ID = 1
        const val ACTION_RUN = "com.maaz.apna.RUN"
        const val ACTION_LISTEN = "com.maaz.apna.LISTEN"
        const val ACTION_STOP_TASK = "com.maaz.apna.STOP_TASK"
        const val ACTION_SHUTDOWN = "com.maaz.apna.SHUTDOWN"
        const val ACTION_REFRESH = "com.maaz.apna.REFRESH"
        const val EXTRA_TASK = "task"
        /** How long to wait for a follow-up after a reply. */

        enum class Phase { OFF, IDLE, LISTENING, WORKING }

        @Volatile var running = false
            private set
        @Volatile var phase = Phase.OFF
            private set
        @Volatile var status = "Off"
            private set
        @Volatile var lastTask: String? = null
            private set
        @Volatile var lastResult: String? = null
            private set
        @androidx.annotation.VisibleForTesting
        fun resetForTest() { lastTask = null; lastResult = null; status = "Off"; phase = Phase.OFF }

        /** Set by the visible activity to mirror status changes. Called on the main thread. */
        var statusListener: ((String) -> Unit)? = null

        /** Returns false if Friday can't start yet because microphone permission is missing. */
        fun start(context: Context, action: String? = null, task: String? = null): Boolean {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
            Settings(context).switchedOn = true
            val intent = Intent(context, ApnaService::class.java).setAction(action).putExtra(EXTRA_TASK, task)
            ContextCompat.startForegroundService(context, intent)
            return true
        }

        /** Turns Friday off and keeps it off until the user turns it on again. */
        fun stop(context: Context) {
            Settings(context).switchedOn = false
            // stopService never creates the service, unlike sending it a "shut down" intent.
            context.stopService(Intent(context, ApnaService::class.java))
        }

        /** For automatic starts (boot, phone control connecting): only if the user left Friday on. */
        fun startIfSwitchedOn(context: Context) {
            if (Settings(context).switchedOn) start(context)
        }
    }
}
