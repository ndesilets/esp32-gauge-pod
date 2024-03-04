package com.example.esp32gauges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esp32gauges.esp32.SensorDataRepository
import com.example.esp32gauges.models.MonitoredSensorData
import com.example.esp32gauges.sensors.MonitoredNumericSensor
import com.example.esp32gauges.sensors.MonitoredPressureSensor
import com.example.esp32gauges.sensors.MonitoredTempSensor
import com.example.esp32gauges.sensors.SupplementalNumericSensor
import com.example.esp32gauges.sensors.status.NumericStatus
import com.example.esp32gauges.sensors.status.PressureStatus
import com.example.esp32gauges.sensors.status.TempStatus
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlin.math.abs

data class MainUiState(
    val monitoredSensorData: MonitoredSensorData = MonitoredSensorData()
)

class MainViewModel(private val repository: SensorDataRepository) : ViewModel() {
    // expose screen ui state
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        monitorSensorData()
    }

    // handle business logic
    @OptIn(FlowPreview::class)
    private fun monitorSensorData() {
        viewModelScope.launch {
            val monitoredSensorDataFlow = repository.getSensorDataStream()
                .transform { sensorData ->
                    // monitored

                    val oilPressure = MonitoredPressureSensor(
                        sensorData.oilPressure, when {
                            sensorData.engineRpm < 500 -> PressureStatus.OK // ignore if engine is off
                            sensorData.oilPressure < 10 -> PressureStatus.CRITICAL
                            else -> PressureStatus.OK
                        }
                    )

                    val oilTemp = MonitoredTempSensor(
                        sensorData.oilTemp, when {
                            sensorData.oilTemp < 170 -> TempStatus.COLD
                            sensorData.oilTemp < 240 -> TempStatus.OK
                            sensorData.oilTemp < 250 -> TempStatus.HOT
                            else -> TempStatus.CRITICAL
                        }
                    )

                    val coolantTemp = MonitoredTempSensor(
                        sensorData.coolantTemp, when {
                            sensorData.coolantTemp < 170 -> TempStatus.COLD
                            sensorData.coolantTemp < 220 -> TempStatus.OK
                            sensorData.coolantTemp < 230 -> TempStatus.HOT
                            else -> TempStatus.CRITICAL
                        }
                    )

                    val boostPressure = MonitoredPressureSensor(
                        sensorData.boostPressure, when {
                            sensorData.boostPressure < 20.5 -> PressureStatus.OK
                            sensorData.boostPressure < 21.5 -> PressureStatus.HIGH
                            else -> PressureStatus.CRITICAL // excess boost creep
                        }
                    )

                    val dynamicAdvanceMultiplier = MonitoredNumericSensor(
                        sensorData.dynamicAdvanceMultiplier, when {
                            sensorData.dynamicAdvanceMultiplier < 1.0 -> NumericStatus.OK
                            else -> NumericStatus.OK
                        }
                    )

                    val fineKnock = MonitoredNumericSensor(
                        sensorData.fineKnock, when {
                            sensorData.fineKnock < 0 -> NumericStatus.CRITICAL
                            else -> NumericStatus.OK
                        }
                    )

                    val feedbackKnock = MonitoredNumericSensor(
                        sensorData.feedbackKnock, when {
                            sensorData.feedbackKnock < -1.4 -> NumericStatus.CRITICAL
                            sensorData.feedbackKnock < 0 -> NumericStatus.WARN
                            else -> NumericStatus.OK
                        }
                    )

                    val afLearn = MonitoredNumericSensor(
                        sensorData.afLearn, when {
                            abs(sensorData.afLearn) < 8 -> NumericStatus.OK
                            abs(sensorData.afLearn) < 10 -> NumericStatus.WARN
                            else -> NumericStatus.CRITICAL
                        }
                    )

                    // supplemental

                    val engineRpm = SupplementalNumericSensor(sensorData.engineRpm)
                    val engineLoad = SupplementalNumericSensor(sensorData.engineLoad)
                    val throttlePosition = SupplementalNumericSensor(sensorData.throttlePosition)

                    emit(
                        MonitoredSensorData(
                            oilPressure,
                            oilTemp,
                            coolantTemp,
                            boostPressure,
                            dynamicAdvanceMultiplier,
                            fineKnock,
                            feedbackKnock,
                            afLearn,

                            engineRpm,
                            engineLoad,
                            throttlePosition
                        )
                    )
                }

            monitoredSensorDataFlow.debounce(10_000).collect {

            }

            monitoredSensorDataFlow
                .collect { monitoredSensorData ->
                    _uiState.value = _uiState.value.copy(monitoredSensorData = monitoredSensorData)
                }
        }
    }
}