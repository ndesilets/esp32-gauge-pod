package com.example.esp32gauges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esp32gauges.models.MonitoredSensorData
import com.example.esp32gauges.services.MonitoringService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val monitoredSensorData: MonitoredSensorData = MonitoredSensorData()
)

class MainViewModel(private val monitoringService: MonitoringService) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        monitorSensorData()
    }

    private fun monitorSensorData() {
        viewModelScope.launch {
            monitoringService.monitored.collect { monitored ->
                _uiState.value = _uiState.value.copy(monitoredSensorData = monitored)
            }
        }
    }

    fun reset() {
        monitoringService.reset()
    }
}