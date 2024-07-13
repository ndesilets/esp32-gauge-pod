package com.example.esp32gauges.models

import com.example.esp32gauges.models.sensors.MonitoredNumericSensor
import com.example.esp32gauges.models.sensors.MonitoredPressureSensor
import com.example.esp32gauges.models.sensors.MonitoredTempSensor
import com.example.esp32gauges.models.sensors.SimpleSensor

data class MonitoredSensorData(
    val coolantTemp: MonitoredTempSensor = MonitoredTempSensor(),
    val oilTemp: MonitoredTempSensor = MonitoredTempSensor(),
    val oilPressure: MonitoredPressureSensor = MonitoredPressureSensor(),
    val fuelPressure: MonitoredPressureSensor = MonitoredPressureSensor(),
    val boostPressure: MonitoredPressureSensor = MonitoredPressureSensor(),
    val dynamicAdvanceMultiplier: MonitoredNumericSensor = MonitoredNumericSensor(),
    val fineKnock: MonitoredNumericSensor = MonitoredNumericSensor(),
    val feedbackKnock: MonitoredNumericSensor = MonitoredNumericSensor(),
    val afCorrection: MonitoredNumericSensor = MonitoredNumericSensor(),
    val afLearn: MonitoredNumericSensor = MonitoredNumericSensor(),
    val afRatio: MonitoredNumericSensor = MonitoredNumericSensor(),

    val engineRpm: SimpleSensor = SimpleSensor(),
    val engineLoad: SimpleSensor = SimpleSensor(),
    val throttlePosition: SimpleSensor = SimpleSensor(),
    val ethanolContent: SimpleSensor = SimpleSensor(),
)
