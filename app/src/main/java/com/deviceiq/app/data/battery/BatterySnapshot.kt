package com.deviceiq.app.data.battery

data class BatterySnapshot(
    val timestamp: Long,
    val level: Int,
    val temperature: Float?,
    val voltage: Int?,
    val isCharging: Boolean
)