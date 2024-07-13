package com.example.esp32gauges.models.sensors

import com.example.esp32gauges.models.sensors.status.NumericStatus


class MonitoredNumericSensor(
    val value: Float = 0f,
    val status: NumericStatus = NumericStatus.OK,
    val summary: Summary = Summary()
)