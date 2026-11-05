package com.maaz.apna.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.maaz.apna.Settings

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED || !Settings(context).startOnBoot) return
        // Newer Android versions may refuse to start a microphone service at boot; the
        // accessibility service retries once it connects.
        runCatching { ApnaService.startIfSwitchedOn(context) }.onFailure { Log.w("Apna", "boot start refused", it) }
    }
}
