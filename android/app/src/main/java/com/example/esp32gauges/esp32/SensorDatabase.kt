package com.example.esp32gauges.esp32

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.esp32gauges.daos.SensorDataEventDao
import com.example.esp32gauges.models.SensorDataEvent

@Database(entities = [SensorDataEvent::class], version = 1)
abstract class SensorDatabase: RoomDatabase() {
    abstract fun sensorDataEventDao(): SensorDataEventDao
}