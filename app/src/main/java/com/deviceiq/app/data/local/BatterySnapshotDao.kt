package com.deviceiq.app.data.battery.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BatterySnapshotDao {

    @Insert
    suspend fun insert(snapshot: BatterySnapshotEntity)

    @Query(
        "SELECT * FROM battery_snapshots " +
                "ORDER BY timestamp DESC"
    )
    suspend fun getAll(): List<BatterySnapshotEntity>

    @Query(
        "SELECT * FROM battery_snapshots " +
                "ORDER BY timestamp DESC LIMIT 1"
    )
    suspend fun getLatest(): BatterySnapshotEntity?

    @Query("DELETE FROM battery_snapshots")
    suspend fun deleteAll()
}