package com.deviceiq.app.data.device

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.StatFs

data class DeviceInfo(
    val batteryPercent: Int?,
    val batteryTemperature: Float?,
    val storageUsedPercent: Int?,
    val storageAvailableGb: Double?,
    val ramUsedPercent: Int?,
    val ramAvailableGb: Double?,
    val deviceModel: String,
    val androidVersion: String
)

class DeviceInfoProvider(
    private val context: Context
) {

    fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            batteryPercent = getBatteryPercent(),
            batteryTemperature = getBatteryTemperature(),
            storageUsedPercent = getStorageUsedPercent(),
            storageAvailableGb = getStorageAvailableGb(),
            ramUsedPercent = getRamUsedPercent(),
            ramAvailableGb = getRamAvailableGb(),
            deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
            androidVersion = Build.VERSION.RELEASE
        )
    }

    private fun getBatteryPercent(): Int? {
        val batteryManager =
            context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
                ?: return null

        val capacity = batteryManager.getIntProperty(
            BatteryManager.BATTERY_PROPERTY_CAPACITY
        )

        return if (capacity in 0..100) capacity else null
    }

    private fun getBatteryTemperature(): Float? {
        val intent = context.registerReceiver(
            null,
            android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)
        ) ?: return null

        val temperature = intent.getIntExtra(
            BatteryManager.EXTRA_TEMPERATURE,
            Int.MIN_VALUE
        )

        return if (temperature != Int.MIN_VALUE) {
            temperature / 10f
        } else {
            null
        }
    }

    private fun getStorageUsedPercent(): Int? {
        val statFs = StatFs(context.filesDir.absolutePath)

        val totalBytes = statFs.totalBytes
        val availableBytes = statFs.availableBytes

        if (totalBytes <= 0) return null

        val usedBytes = totalBytes - availableBytes

        return ((usedBytes.toDouble() / totalBytes) * 100)
            .toInt()
            .coerceIn(0, 100)
    }

    private fun getStorageAvailableGb(): Double? {
        val statFs = StatFs(context.filesDir.absolutePath)

        val availableBytes = statFs.availableBytes

        if (availableBytes < 0) return null

        return availableBytes / 1024.0 / 1024.0 / 1024.0
    }

    private fun getRamUsedPercent(): Int? {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                ?: return null

        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        if (memoryInfo.totalMem <= 0) return null

        val usedMemory = memoryInfo.totalMem - memoryInfo.availMem

        return ((usedMemory.toDouble() / memoryInfo.totalMem) * 100)
            .toInt()
            .coerceIn(0, 100)
    }

    private fun getRamAvailableGb(): Double? {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                ?: return null

        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        return memoryInfo.availMem / 1024.0 / 1024.0 / 1024.0
    }
}