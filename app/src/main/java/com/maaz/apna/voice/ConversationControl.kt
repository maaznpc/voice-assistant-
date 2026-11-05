package com.maaz.apna.voice

/** Local control commands never need an LLM request. Whole phrases avoid accidental task cancellation. */
object ConversationControl {
    fun isSleep(text: String): Boolean = VoiceInput.words(text).joinToString(" ") in setOf(
        "sleep", "go to sleep", "sleep now", "standby", "stand by", "go to standby",
        "goodbye", "good bye", "that's all", "thats all", "so jao", "so ja",
        "سو جاؤ", "سو جاو", "اب سو جاؤ", "بس کرو", "خدا حافظ", "اللہ حافظ", "فرائیڈے سو جاؤ",
        "सो जाओ", "बस करो", "फ्राइडे सो जाओ", "bas karo", "khuda hafiz", "allah hafiz",
        "friday sleep", "friday go to sleep", "friday so jao",
        "apna sleep", "apna go to sleep", "apna so jao", // Existing users may still use the old name.
    )
    fun timeoutMs(seconds: Int) = seconds.coerceIn(5, 60) * 1000L
}
