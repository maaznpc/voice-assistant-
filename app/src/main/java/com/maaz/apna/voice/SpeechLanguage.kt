package com.maaz.apna.voice

/** One automatic language policy; older per-language preferences no longer affect conversation. */
enum class SpeechLanguage {
    AUTO;
    val locale get() = "ur-PK" // Initial hint only for legacy Android recognition services.
    val isOffline get() = false
    val needsSpokenText get() = true
    val preview get() = "السلام علیکم، میں فرائیڈے ہوں۔ آپ اردو، ہندی یا English میں بات کر سکتے ہیں۔"
    val prompt get() = """
Automatically infer the user's language and script from the CURRENT request and conversation, including English, Urdu, Roman Urdu, Hindi and Hinglish. No language selection is needed. Code-switch naturally and honor explicit requests to change language. Roman Urdu and Hinglish overlap: don't force a distinction or ask which language they are using. For ambiguous Latin-script South Asian speech use natural Roman Urdu/Hinglish matching their words. Use Urdu script for Urdu-script requests, Devanagari for Hindi-script requests and English for English requests. Preserve contact names, numbers, app names and message content.
Always provide spoken_summary with finish and spoken_question with ask_user, with exactly the same meaning as summary/question. For Roman Urdu use Urdu script in the spoken field; for Hinglish use Devanagari; for English keep English. Use conversation context to choose Urdu or Hindi pronunciation when mixed Latin text is ambiguous. Never send Roman Urdu to an English voice as a pronunciation substitute.
""".trimIndent()
    companion object {
        fun ttsLocale(text: String): String = when {
            text.any { it.isLetter() && it in '\u0600'..'\u06ff' } -> "ur-PK"
            text.any { it.isLetter() && it in '\u0900'..'\u097f' } -> "hi-IN"
            else -> "en-US"
        }
    }
}
