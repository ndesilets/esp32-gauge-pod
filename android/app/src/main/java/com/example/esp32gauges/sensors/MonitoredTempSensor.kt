package com.example.esp32gauges.sensors

enum class TempStatus { COLD, OK, HOT, CRITICAL }
class MonitoredTempSensor(val value: Float = 0f, val status: TempStatus = TempStatus.OK)
