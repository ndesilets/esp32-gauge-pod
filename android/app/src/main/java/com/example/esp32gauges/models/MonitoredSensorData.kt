package com.example.esp32gauges.models

import com.example.esp32gauges.sensors.MonitoredNumericSensor
import com.example.esp32gauges.sensors.MonitoredPressureSensor
import com.example.esp32gauges.sensors.MonitoredTempSensor
import com.example.esp32gauges.sensors.SupplementalNumericSensor

data class MonitoredSensorData(
    val oilPressure: MonitoredPressureSensor = MonitoredPressureSensor(),
    val oilTemp: MonitoredTempSensor = MonitoredTempSensor(),
    val coolantTemp: MonitoredTempSensor = MonitoredTempSensor(),
    val fuelPressure: MonitoredPressureSensor = MonitoredPressureSensor(),
    val fuelPressureHistory: List<Float> = listOf(),
    val ethanolContent: SupplementalNumericSensor = SupplementalNumericSensor(),
    val boostPressure: MonitoredPressureSensor = MonitoredPressureSensor(),

    val dynamicAdvanceMultiplier: MonitoredNumericSensor = MonitoredNumericSensor(),
    val dynamicAdvanceMultiplierHistory: List<Float> = listOf(),

    val fineKnock: MonitoredNumericSensor = MonitoredNumericSensor(),
    val feedbackKnock: MonitoredNumericSensor = MonitoredNumericSensor(),
    val afCorrection: MonitoredNumericSensor = MonitoredNumericSensor(),
    val afLearn: MonitoredNumericSensor = MonitoredNumericSensor(),
    val afRatio: MonitoredNumericSensor = MonitoredNumericSensor(),

    val engineRpm: SupplementalNumericSensor = SupplementalNumericSensor(),
    val engineLoad: SupplementalNumericSensor = SupplementalNumericSensor(),
    val throttlePosition: SupplementalNumericSensor = SupplementalNumericSensor(),
)
