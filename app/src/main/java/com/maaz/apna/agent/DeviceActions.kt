package com.maaz.apna.agent

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.provider.AlarmClock
import org.json.JSONObject

/** Small deterministic Android actions; arbitrary intents, shell commands and code are not accepted. */
class DeviceActions(private val context: Context) {
    fun execute(a: JSONObject): String = when (a.getString("action")) {
        "battery" -> {
            val percent = context.getSystemService(BatteryManager::class.java)
                .getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            if (percent in 0..100) "Battery: $percent%." else "Battery level unavailable."
        }
        "volume" -> {
            val percent = a.getInt("value").also { require(it in 0..100) { "Volume must be 0–100." } }
            val audio = context.getSystemService(AudioManager::class.java)
            audio.setStreamVolume(AudioManager.STREAM_MUSIC,
                (audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * percent / 100f).toInt(), AudioManager.FLAG_SHOW_UI)
            "Media volume set to $percent percent."
        }
        "open_url" -> launch(Intent(Intent.ACTION_VIEW, validatedWebUri(a.getString("text"))), "Opened the website.")
        "search_web" -> launch(Intent(Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/search").buildUpon().appendQueryParameter("q", a.getString("text")).build()),
            "Opened web search results; read the screen to answer.")
        "dial" -> launch(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber(a.getString("text")), null)),
            "Opened the dialer. The call has not been placed.")
        "draft_sms" -> launch(Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", phoneNumber(a.getString("text")), null))
            .putExtra("sms_body", a.getString("body")), "Opened an SMS draft. It has not been sent.")
        "timer" -> {
            val seconds = a.getInt("value").also { require(it in 1..86400) { "Timer must be 1–86400 seconds." } }
            launch(Intent(AlarmClock.ACTION_SET_TIMER).putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                .putExtra(AlarmClock.EXTRA_MESSAGE, a.optString("text", "Friday timer"))
                .putExtra(AlarmClock.EXTRA_SKIP_UI, false), "Opened the clock timer request; verify its screen.")
        }
        "alarm" -> {
            val hour = a.getInt("hour").also { require(it in 0..23) { "Hour must be 0–23." } }
            val minute = a.getInt("minute").also { require(it in 0..59) { "Minute must be 0–59." } }
            launch(Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minute).putExtra(AlarmClock.EXTRA_MESSAGE, a.optString("text", "Friday alarm"))
                .putExtra(AlarmClock.EXTRA_SKIP_UI, false), "Opened the clock alarm request; verify its screen.")
        }
        else -> throw IllegalArgumentException("Unknown device action.")
    }

    private fun launch(intent: Intent, result: String): String {
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return result
    }

    companion object {
        fun phoneNumber(text: String): String {
            require(Regex("\\+?[0-9 ()-]{3,30}").matches(text)) { "Use a phone number, or find the named contact in the phone app." }
            val normalized = text.filter { it.isDigit() || it == '+' }
            require(normalized.count { it.isDigit() } >= 3) { "A phone number needs at least three digits." }
            return normalized
        }
        fun validatedWebUri(text: String): Uri {
            val uri = Uri.parse(text)
            require(uri.scheme in setOf("https", "http") && !uri.host.isNullOrBlank() && uri.userInfo == null) {
                "Only HTTP(S) website addresses without credentials are supported."
            }
            return uri
        }
    }
}
