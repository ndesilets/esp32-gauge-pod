package com.example.esp32gauges.sensors

import com.example.esp32gauges.sensors.status.NumericStatus


class MonitoredNumericSensor(val value: Float = 0f, val status: NumericStatus = NumericStatus.OK)