package com.example.esp32gauges.esp32

import com.example.esp32gauges.models.OBDIISensorDataEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.sin

class MockedESP32DataSource : ESP32DataSourceI {
    private var phase = 0.0

    // generates an oscillating value between min and max
    private fun generateValue(phase: Double, min: Float, max: Float): Float {
        return ((sin(phase) * 0.5 + 0.5) * (max - min) + min).toFloat()
    }

    override fun streamSensorData(): Flow<OBDIISensorDataEvent> = flow {
        while (true) {
            val OBDIISensorDataEvent = OBDIISensorDataEvent(
                timestamp = System.currentTimeMillis(),

                oilPressure = generateValue(phase, 0f, 100f),
                oilTemp = generateValue(phase, -20f, 300f),
                coolantTemp = generateValue(phase, -20f, 300f),
                fuelPressure = generateValue(phase, 0f, 60f),
                ethanolContent = generateValue(phase, 0f, 100f),

                airFuelRatio = generateValue(phase, 10.8f, 20f),
                boostPressure = generateValue(phase, -14f, 23f),

                dynamicAdvanceMultiplier = generateValue(phase, 0f, 1f),
                fineKnockLearn = generateValue(phase, -4f, 0f),
                feedbackKnock = generateValue(phase, -4f, 0f),
                afCorrection = generateValue(phase, -20f, 20f),
                afLearn = generateValue(phase, -20f, 20f),
                afRatio = generateValue(phase, 8.0f, 20f),

                engineRpm = generateValue(phase, 0f, 7000f),
                engineLoad = generateValue(phase, 0f, 100f),
                throttlePosition = generateValue(phase, 0f, 100f)
            )

            emit(OBDIISensorDataEvent)
            phase += 0.01
            delay(33)
        }
    }
}