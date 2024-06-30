package com.example.esp32gauges.sensors

import com.example.esp32gauges.sensors.status.PressureStatus

data class MonitoredPressureSensor(
    val value: Float = 0f,
    val status: PressureStatus = PressureStatus.OK
)