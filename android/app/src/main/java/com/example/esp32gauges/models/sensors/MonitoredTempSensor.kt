package com.example.esp32gauges.models.sensors

import com.example.esp32gauges.models.sensors.status.TempStatus

class MonitoredTempSensor(
    val value: Float = 0f,
    val status: TempStatus = TempStatus.OK,
    val summary: Summary = Summary()
)
