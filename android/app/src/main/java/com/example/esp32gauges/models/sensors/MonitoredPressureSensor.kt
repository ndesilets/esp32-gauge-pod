package com.example.esp32gauges.models.sensors

import com.example.esp32gauges.models.sensors.status.PressureStatus

data class MonitoredPressureSensor(
    val value: Float = 0f,
    val status: PressureStatus = PressureStatus.OK,
    val summary: Summary = Summary()
)