package com.example.esp32gauges.repositories.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.esp32gauges.models.OBDIISensorDataEvent

@Dao
interface SensorDataEventDao {
    @Query("select * from sensor_data_event order by timestamp limit 1")
    fun getLatest(): OBDIISensorDataEvent

    @Query("select * from sensor_data_event where timestamp >= :timeLimit order by timestamp")
    fun getLastNMillis(timeLimit: Long): List<OBDIISensorDataEvent>

    @Insert
    fun insert(OBDIISensorDataEvent: OBDIISensorDataEvent)
}