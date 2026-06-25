package com.symbolkeyboard.util

import android.content.Context
import android.os.PowerManager

class PowerSaver(private val context: Context) {

    private val powerManager: PowerManager? =
        context.getSystemService(Context.POWER_SERVICE) as? PowerManager

    fun isPowerSaveMode(): Boolean {
        return powerManager?.isPowerSaveMode ?: false
    }
}
