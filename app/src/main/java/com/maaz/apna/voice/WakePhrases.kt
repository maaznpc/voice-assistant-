package com.maaz.apna.voice

/** Token sequences generated with the downloaded KWS model's own bpe.model. */
data class WakePhrase(val id: String, val label: String, val tokens: String) {
    fun keywordLine() = "$tokens :2.0 #0.2 @${id.uppercase()}\n"
}

object WakePhrases {
    val all = listOf(
        WakePhrase("apna", "Hey Apna", "▁HE Y ▁A P N A"),
        WakePhrase("assistant", "Hey Assistant", "▁HE Y ▁AS S IST ANT"),
        WakePhrase("computer", "Hey Computer", "▁HE Y ▁COMP U TER"),
    )
    fun find(id: String) = all.firstOrNull { it.id == id } ?: all.first()
}
