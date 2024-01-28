package com.example.esp32gauges.esp32

import com.example.esp32gauges.models.SensorData
import kotlinx.coroutines.flow.Flow

class SensorDataRepository(private val dataSource: ESP32DataSourceI) {
    fun getSensorDataStream(): Flow<SensorData> = dataSource.streamSensorData()
}