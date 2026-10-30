package com.deviceiq.app.data.battery

import com.deviceiq.app.data.battery.local.BatterySnapshotDao
import com.deviceiq.app.data.battery.local.BatterySnapshotEntity

class BatteryRepository(
    private val batteryInfoProvider: BatteryInfoProvider,
    private val batterySnapshotDao: BatterySnapshotDao
) {

    suspend fun collectAndSaveSnapshot() {
        val snapshot = batteryInfoProvider.getSnapshot()
            ?: return

        batterySnapshotDao.insert(
            BatterySnapshotEntity(
                timestamp = snapshot.timestamp,
                level = snapshot.level,
                temperature = snapshot.temperature,
                voltage = snapshot.voltage,
                isCharging = snapshot.isCharging
            )
        )
    }

    suspend fun getLatestSnapshot(): BatterySnapshotEntity? {
        return batterySnapshotDao.getLatest()
    }

    suspend fun getAllSnapshots(): List<BatterySnapshotEntity> {
        return batterySnapshotDao.getAll()
    }
}