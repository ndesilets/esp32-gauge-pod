package com.example.esp32gauges.sensors

enum class PressureStatus { LOW, OK, HIGH, CRITICAL }
data class MonitoredPressureSensor(val value: Float = 0f, val status: PressureStatus = PressureStatus.OK)