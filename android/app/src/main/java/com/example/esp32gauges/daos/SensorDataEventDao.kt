package com.example.esp32gauges.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.esp32gauges.models.SensorDataEvent

@Dao
interface SensorDataEventDao {
    @Query("SELECT * FROM sensor_data_event ORDER BY timestamp LIMIT 1")
    fun getLatest(): SensorDataEvent
}