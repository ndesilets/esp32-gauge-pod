package com.example.esp32gauges.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_data_event")
data class OBDIISensorDataEvent (
    @PrimaryKey val timestamp: Long,

    @ColumnInfo(name = "oil_pressure") val oilPressure: Float,
    @ColumnInfo(name = "oil_temp") val oilTemp: Float,
    @ColumnInfo(name = "coolant_temp") val coolantTemp: Float,

    @ColumnInfo(name = "air_fuel_ratio") val airFuelRatio: Float,
    @ColumnInfo(name = "boost_pressure") val boostPressure: Float,

    @ColumnInfo(name = "dynamic_advance_multiplier") val dynamicAdvanceMultiplier: Float,
    @ColumnInfo(name = "fine_knock") val fineKnock: Float,
    @ColumnInfo(name = "feedback_knock") val feedbackKnock: Float,
    @ColumnInfo(name = "af_learn") val afLearn: Float,

    @ColumnInfo(name = "engine_rpm") val engineRpm: Float,
    @ColumnInfo(name = "engine_load") val engineLoad: Float,
    @ColumnInfo(name = "throttle_position") val throttlePosition: Float
)