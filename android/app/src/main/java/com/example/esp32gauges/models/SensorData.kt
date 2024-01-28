package com.example.esp32gauges.models

data class SensorData (
    val oilPressure: Float,
    val oilTemp: Float,
    val coolantTemp: Float,
    val boostPressure: Float,

    val dynamicAdvanceMultiplier: Float,
    val fineKnock: Float,
    val feedbackKnock: Float,
    val afLearn: Float,

    val engineRpm: Float,
    val engineLoad: Float,
    val throttlePosition: Float
)