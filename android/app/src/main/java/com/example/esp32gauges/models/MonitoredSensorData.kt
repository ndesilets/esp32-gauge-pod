package com.example.esp32gauges.models

import com.example.esp32gauges.sensors.MonitoredNumericSensor
import com.example.esp32gauges.sensors.MonitoredPressureSensor
import com.example.esp32gauges.sensors.MonitoredTempSensor
import com.example.esp32gauges.sensors.SupplementalNumericSensor

data class MonitoredSensorData(
    val oilPressure: MonitoredPressureSensor = MonitoredPressureSensor(),
    val oilTemp: MonitoredTempSensor = MonitoredTempSensor(),
    val coolantTemp: MonitoredTempSensor = MonitoredTempSensor(),
    val boostPressure: MonitoredPressureSensor = MonitoredPressureSensor(),

    val dynamicAdvanceMultiplier: MonitoredNumericSensor = MonitoredNumericSensor(),
    val fineKnock: MonitoredNumericSensor = MonitoredNumericSensor(),
    val feedbackKnock: MonitoredNumericSensor = MonitoredNumericSensor(),
    val afLearn: MonitoredNumericSensor = MonitoredNumericSensor(),

    val engineRpm: SupplementalNumericSensor = SupplementalNumericSensor(),
    val engineLoad: SupplementalNumericSensor = SupplementalNumericSensor(),
    val throttlePosition: SupplementalNumericSensor = SupplementalNumericSensor(),
)
