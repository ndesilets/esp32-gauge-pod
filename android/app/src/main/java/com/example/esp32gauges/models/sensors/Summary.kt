package com.example.esp32gauges.models.sensors

// TODO 1m/5m/session min/max
data class Summary(
//    val min1m: Float = 0f,
//    val max1m: Float = 0f,
//    val min15m: Float = 0f,
//    val max15m: Float = 0f,
    val minSession: Float = 0f,
    val maxSession: Float = 0f
)
