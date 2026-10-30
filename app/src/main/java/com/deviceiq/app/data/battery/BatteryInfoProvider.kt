package com.deviceiq.app.data.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatteryInfoProvider(
    private val context: Context
) {

    fun getSnapshot(): BatterySnapshot? {

        val intent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ) ?: return null

        val level = intent.getIntExtra(
            BatteryManager.EXTRA_LEVEL,
            -1
        )

        val scale = intent.getIntExtra(
            BatteryManager.EXTRA_SCALE,
            -1
        )

        if (level < 0 || scale <= 0) {
            return null
        }

        val batteryPercent =
            (level * 100 / scale).coerceIn(0, 100)

        val temperatureRaw = intent.getIntExtra(
            BatteryManager.EXTRA_TEMPERATURE,
            Int.MIN_VALUE
        )

        val temperature =
            if (temperatureRaw != Int.MIN_VALUE) {
                temperatureRaw / 10f
            } else {
                null
            }

        val voltage = intent.getIntExtra(
            BatteryManager.EXTRA_VOLTAGE,
            -1
        ).takeIf { it > 0 }

        val status = intent.getIntExtra(
            BatteryManager.EXTRA_STATUS,
            -1
        )

        val isCharging =
            status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

        return BatterySnapshot(
            timestamp = System.currentTimeMillis(),
            level = batteryPercent,
            temperature = temperature,
            voltage = voltage,
            isCharging = isCharging
        )
    }
}