package com.example.esp32gauges

import com.example.esp32gauges.models.SensorData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.sin

class MockedESP32DataSource : ESP32DataSourceI {
    private var phase = 0.0

    private fun generateValue(phase: Double, min: Float, max: Float) : Float {
        return ((sin(phase) * 0.5 + 0.5) * (max - min) + min).toFloat()
    }

    override fun streamSensorData(): Flow<SensorData> = flow {
        while (true) {
            val sensorData = SensorData(
                generateValue(phase, 0f, 100f),
                generateValue(phase, -20f, 300f),
                generateValue(phase, -20f, 300f),
                generateValue(phase, -14f, 23f),

                generateValue(phase,0f, 1f),
                generateValue(phase, -4f, 0f),
                generateValue(phase, -4f, 0f),
                generateValue(phase, -20f, 20f),

                generateValue(phase, 0f, 7000f),
                generateValue(phase, 0f, 100f),
                generateValue(phase, 0f, 100f)
            )

            emit (sensorData)
            phase += 0.01
            delay(33)
        }
    }
}