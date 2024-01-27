package com.example.esp32gauges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val sensorData: SensorData? = null
)

class MainViewModel(private val repository: SensorDataRepository) : ViewModel() {
    // expose screen ui state
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        monitorSensorData()
    }

    // handle business logic
    private fun monitorSensorData() {
        viewModelScope.launch {
            repository.getSensorDataStream().collect { sensorData ->
                _uiState.value = _uiState.value.copy(sensorData = sensorData)
            }
        }
    }


}