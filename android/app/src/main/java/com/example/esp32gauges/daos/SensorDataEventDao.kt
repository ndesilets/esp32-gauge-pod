package com.example.esp32gauges.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.esp32gauges.models.OBDIISensorDataEvent

@Dao
interface SensorDataEventDao {
    @Query("SELECT * FROM sensor_data_event ORDER BY timestamp LIMIT 1")
    fun getLatest(): OBDIISensorDataEvent

    @Query("SELECT * FROM sensor_data_event WHERE timestamp >= :timeLimit ORDER BY timestamp")
    fun getLastNMillis(timeLimit: Long): List<OBDIISensorDataEvent>

    @Insert
    fun insert(OBDIISensorDataEvent: OBDIISensorDataEvent)
}