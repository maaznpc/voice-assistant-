package com.maaz.apna.voice

/** Local control commands never need an LLM request. Whole phrases avoid accidental task cancellation. */
object ConversationControl {
    fun isSleep(text: String): Boolean = VoiceInput.words(text).joinToString(" ") in setOf(
        "sleep", "go to sleep", "sleep now", "standby", "stand by", "go to standby",
        "goodbye", "good bye", "that's all", "thats all", "so jao", "so ja",
        "apna sleep", "apna go to sleep", "apna so jao",
    )
    fun timeoutMs(seconds: Int) = seconds.coerceIn(5, 60) * 1000L
}
