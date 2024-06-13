package com.example.esp32gauges.daos

import android.util.Log
import com.example.esp32gauges.models.SensorDataEvent

class MockSensorDataEventDao : SensorDataEventDao {
    private val events = mutableListOf<SensorDataEvent>()

    override fun getLatest(): SensorDataEvent {
        return events.maxByOrNull { it.timestamp }
            ?: throw NoSuchElementException("No data available")
    }

    override fun getLastNMillis(timeLimit: Long): List<SensorDataEvent> {
        val currentTime = System.currentTimeMillis()
        return events.filter { it.timestamp >= currentTime - timeLimit }.sortedBy { it.timestamp }
    }

    override fun insert(sensorDataEvent: SensorDataEvent) {
        events.add(sensorDataEvent)
    }
}