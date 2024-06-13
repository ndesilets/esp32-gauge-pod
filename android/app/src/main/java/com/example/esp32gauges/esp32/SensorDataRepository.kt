package com.example.esp32gauges.esp32

import android.util.Log
import com.example.esp32gauges.daos.SensorDataEventDao
import com.example.esp32gauges.models.SensorDataEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SensorDataRepository(private val dataSource: ESP32DataSourceI, private val sensorDataEventDao: SensorDataEventDao) {
    fun getSensorDataStream(): Flow<SensorDataEvent> = dataSource.streamSensorData()
        .onEach { sensorDataEvent ->
            CoroutineScope(Dispatchers.IO).launch {
                sensorDataEventDao.insert(sensorDataEvent)
            }
        }
}