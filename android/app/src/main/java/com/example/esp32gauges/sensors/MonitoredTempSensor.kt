package com.example.esp32gauges.sensors

import com.example.esp32gauges.sensors.status.TempStatus

class MonitoredTempSensor(val value: Float = 0f, val status: TempStatus = TempStatus.OK)
