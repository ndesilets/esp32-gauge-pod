package com.example.esp32gauges.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.esp32gauges.models.SensorDataEvent

@Dao
interface SensorDataEventDao {
    @Query("SELECT * FROM sensor_data_event ORDER BY timestamp LIMIT 1")
    fun getLatest(): SensorDataEvent

    @Query("SELECT * FROM sensor_data_event WHERE timestamp >= :timeLimit ORDER BY timestamp")
    fun getLastNMillis(timeLimit: Long): List<SensorDataEvent>

    @Insert
    fun insert(sensorDataEvent: SensorDataEvent)
}