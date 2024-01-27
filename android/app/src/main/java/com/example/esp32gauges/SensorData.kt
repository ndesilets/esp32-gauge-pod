package com.example.esp32gauges

data class SensorData (
    val oilPressure: Float,
    val oilTemperature: Float,
    val coolantTemperature: Float,
    val boostPsi: Float,

    val dynamicAdvanceMultiplier: Float,
    val fineKnock: Float,
    val feedbackKnock: Float,
    val afLearn: Float,

    val engineRpm: Float,
    val engineLoad: Float,
    val throttlePosition: Float
)