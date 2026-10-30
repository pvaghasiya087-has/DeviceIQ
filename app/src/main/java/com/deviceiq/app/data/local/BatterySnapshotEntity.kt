package com.deviceiq.app.data.battery.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "battery_snapshots")
data class BatterySnapshotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val level: Int,
    val temperature: Float?,
    val voltage: Int?,
    val isCharging: Boolean
)