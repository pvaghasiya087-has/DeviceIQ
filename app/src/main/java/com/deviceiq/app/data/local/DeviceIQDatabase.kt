package com.deviceiq.app.data.battery.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BatterySnapshotEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DeviceIQDatabase : RoomDatabase() {

    abstract fun batterySnapshotDao(): BatterySnapshotDao

    companion object {

        @Volatile
        private var INSTANCE: DeviceIQDatabase? = null

        fun getInstance(context: Context): DeviceIQDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DeviceIQDatabase::class.java,
                    "deviceiq_database"
                )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}