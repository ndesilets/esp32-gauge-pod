package com.example.esp32gauges

import com.example.esp32gauges.models.SensorData
import kotlinx.coroutines.flow.Flow

interface ESP32DataSourceI {
    fun streamSensorData(): Flow<SensorData>
}