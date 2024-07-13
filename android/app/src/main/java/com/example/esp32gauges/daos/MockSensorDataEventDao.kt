package com.example.esp32gauges.daos

import com.example.esp32gauges.models.OBDIISensorDataEvent

class MockSensorDataEventDao : SensorDataEventDao {
    private val events = mutableListOf<OBDIISensorDataEvent>()

    override fun getLatest(): OBDIISensorDataEvent {
        return events.maxByOrNull { it.timestamp }
            ?: throw NoSuchElementException("No data available")
    }

    override fun getLastNMillis(timeLimit: Long): List<OBDIISensorDataEvent> {
        val currentTime = System.currentTimeMillis()
        return events.filter { it.timestamp >= currentTime - timeLimit }.sortedBy { it.timestamp }
    }

    override fun insert(OBDIISensorDataEvent: OBDIISensorDataEvent) {
        events.add(OBDIISensorDataEvent)
    }
}