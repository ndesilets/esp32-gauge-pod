package com.example.esp32gauges

import kotlinx.coroutines.flow.Flow

interface ESP32DataSourceI {
    fun streamSensorData(): Flow<SensorData>
}