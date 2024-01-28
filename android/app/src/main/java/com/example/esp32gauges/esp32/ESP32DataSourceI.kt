package com.example.esp32gauges.esp32

import com.example.esp32gauges.models.SensorData
import kotlinx.coroutines.flow.Flow

interface ESP32DataSourceI {
    fun streamSensorData(): Flow<SensorData>
}