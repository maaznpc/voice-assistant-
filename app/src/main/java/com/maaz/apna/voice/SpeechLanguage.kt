package com.maaz.apna.voice

/** Script choices affect replies; Roman Urdu and Urdu share a spoken language. */
enum class SpeechLanguage(val id: String, val label: String, val locale: String, val replyInstruction: String, val preview: String) {
    ENGLISH("english", "English · offline", "en-US", "Reply in English unless the user requests another language.", "Hello, I am Friday. How can I help you?"),
    URDU("urdu", "اردو · Urdu", "ur-PK", "Reply in Urdu script, preserving app names and code in their original spelling.", "السلام علیکم، میں فرائیڈے ہوں۔ میں آپ کی کیا مدد کر سکتی ہوں؟"),
    ROMAN_URDU("roman_urdu", "Roman Urdu", "ur-PK", "Write summary and question in natural Roman Urdu (Latin letters). Always also provide spoken_summary or spoken_question in Urdu script with the SAME meaning for speech synthesis; never read Roman Urdu with an English accent.", "السلام علیکم، میں فرائیڈے ہوں۔ میں آپ کی کیا مدد کر سکتی ہوں؟"),
    HINDI("hindi", "हिन्दी · Hindi", "hi-IN", "Reply in Hindi using Devanagari script.", "नमस्ते, मैं फ्राइडे हूँ। मैं आपकी क्या मदद कर सकती हूँ?"),
    HINGLISH("hinglish", "Hinglish", "hi-IN", "Write summary and question in natural Hinglish (Latin letters). Always also provide spoken_summary or spoken_question in Devanagari with the SAME meaning for speech synthesis.", "नमस्ते, मैं फ्राइडे हूँ। बताइए मैं क्या कर सकती हूँ?" );

    val isOffline get() = this == ENGLISH
    val needsSpokenText get() = this == ROMAN_URDU || this == HINGLISH
    val prompt get() = "Understand English, Urdu, Roman Urdu, Hindi and mixed-language requests. Roman Urdu is Urdu written in Latin letters. Interpret requests by meaning and preserve contact names, numbers and message content. $replyInstruction"

    companion object {
        fun find(id: String?) = entries.firstOrNull { it.id == id } ?: ENGLISH
        fun ttsLocale(text: String): String = when {
            text.any { it in '\u0600'..'\u06ff' } -> "ur-PK"
            text.any { it in '\u0900'..'\u097f' } -> "hi-IN"
            else -> "en-US" // Roman replies supply a separate native-script spoken text.
        }
    }
}
