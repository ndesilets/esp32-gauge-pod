package com.example.esp32gauges.repositories

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.esp32gauges.daos.SensorDataEventDao
import com.example.esp32gauges.models.OBDIISensorDataEvent

@Database(entities = [OBDIISensorDataEvent::class], version = 1)
abstract class SensorDatabase: RoomDatabase() {
    abstract fun sensorDataEventDao(): SensorDataEventDao
}