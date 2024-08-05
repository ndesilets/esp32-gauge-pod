package com.example.esp32gauges.services

import com.example.esp32gauges.models.MonitoredSensorData
import com.example.esp32gauges.models.sensors.MonitoredNumericSensor
import com.example.esp32gauges.models.sensors.MonitoredPressureSensor
import com.example.esp32gauges.models.sensors.MonitoredTempSensor
import com.example.esp32gauges.models.sensors.SimpleSensor
import com.example.esp32gauges.models.sensors.Summary
import com.example.esp32gauges.models.sensors.status.NumericStatus
import com.example.esp32gauges.models.sensors.status.PressureStatus
import com.example.esp32gauges.models.sensors.status.TempStatus
import com.example.esp32gauges.repositories.SensorDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MonitoringService(val dataRepository: SensorDataRepository) {
    private val _monitored = MutableSharedFlow<MonitoredSensorData>()
    private var dataCollectionJob: Job? = null
    private var previousMonitoredSensorData = MonitoredSensorData()
    val monitored = _monitored.asSharedFlow()

    private fun calcCoolantTempMonitor(temp: Float): MonitoredTempSensor {
        return MonitoredTempSensor(
            value = temp,
            status = when {
                temp < 170 -> TempStatus.COLD
                temp < 220 -> TempStatus.OK
                temp < 230 -> TempStatus.HOT
                else -> TempStatus.CRITICAL
            },
            summary = Summary(
                minSession = min(previousMonitoredSensorData.coolantTemp.summary.minSession, temp),
                maxSession = max(previousMonitoredSensorData.coolantTemp.summary.maxSession, temp)
            )
        )
    }

    private fun calcOilPressureMonitor(psi: Float, engineRpm: Float): MonitoredPressureSensor {
        return MonitoredPressureSensor(
            value = psi,
            status = when {
                engineRpm < 500 -> PressureStatus.OK // ignore if engine is off
                psi < 10 -> PressureStatus.CRITICAL
                else -> PressureStatus.OK
            },
            summary = Summary(
                minSession = min(previousMonitoredSensorData.oilPressure.summary.minSession, psi),
                maxSession = max(previousMonitoredSensorData.oilPressure.summary.maxSession, psi)
            )
        )
    }

    private fun calcOilTempMonitor(temp: Float): MonitoredTempSensor {
        return MonitoredTempSensor(
            value = temp,
            status = when {
                temp < 170 -> TempStatus.COLD
                temp < 240 -> TempStatus.OK
                temp < 250 -> TempStatus.HOT
                else -> TempStatus.CRITICAL
            },
            summary = Summary(
                minSession = min(previousMonitoredSensorData.oilTemp.summary.minSession, temp),
                maxSession = max(previousMonitoredSensorData.oilTemp.summary.maxSession, temp)
            )
        )
    }

    private fun calcFuelPressureMonitor(psi: Float, engineRpm: Float): MonitoredPressureSensor {
        return MonitoredPressureSensor(
            value = psi,
            status = when {
                engineRpm < 500 -> PressureStatus.OK // ignore if engine is off
                psi < 30 -> PressureStatus.CRITICAL
                else -> PressureStatus.OK
            },
            summary = Summary(
                minSession = min(previousMonitoredSensorData.fuelPressure.summary.minSession, psi),
                maxSession = max(previousMonitoredSensorData.fuelPressure.summary.maxSession, psi)
            )
        )
    }

    private fun calcBoostPressureMonitor(psi: Float): MonitoredPressureSensor {
        return MonitoredPressureSensor(
            value = psi,
            status = when {
                psi < 20.5 -> PressureStatus.OK
                psi < 21.5 -> PressureStatus.HIGH
                else -> PressureStatus.CRITICAL // excess boost creep
            },
            summary = Summary(
                minSession = min(previousMonitoredSensorData.boostPressure.summary.minSession, psi),
                maxSession = max(previousMonitoredSensorData.boostPressure.summary.maxSession, psi)
            )
        )
    }

    private fun calcDamMonitor(dam: Float): MonitoredNumericSensor {
        return MonitoredNumericSensor(
            value = dam,
            status = when {
                dam == 1.0f -> NumericStatus.OK
                else -> NumericStatus.CRITICAL
            },
            summary = Summary(
                minSession = min(
                    previousMonitoredSensorData.dynamicAdvanceMultiplier.summary.minSession,
                    dam
                ),
                maxSession = max(
                    previousMonitoredSensorData.dynamicAdvanceMultiplier.summary.maxSession,
                    dam
                )
            )
        )
    }

    private fun calcFineKnockLearnMonitor(fineKnockLearn: Float): MonitoredNumericSensor {
        return MonitoredNumericSensor(
            value = fineKnockLearn,
            status = when {
                fineKnockLearn < -4.2 -> NumericStatus.CRITICAL
                else -> NumericStatus.OK
            },
            summary = Summary(
                minSession = min(
                    previousMonitoredSensorData.fineKnockLearn.summary.minSession,
                    fineKnockLearn
                ),
                maxSession = max(
                    previousMonitoredSensorData.fineKnockLearn.summary.maxSession,
                    fineKnockLearn
                )
            )
        )
    }

    private fun calcFeedbackKnockMonitor(feedbackKnock: Float): MonitoredNumericSensor {
        return MonitoredNumericSensor(
            value = feedbackKnock,
            status = when {
                feedbackKnock < -2.8 -> NumericStatus.CRITICAL
                feedbackKnock < 0 -> NumericStatus.WARN
                else -> NumericStatus.OK
            },
            summary = Summary(
                minSession = min(
                    previousMonitoredSensorData.feedbackKnock.summary.minSession,
                    feedbackKnock
                ),
                maxSession = max(
                    previousMonitoredSensorData.feedbackKnock.summary.maxSession,
                    feedbackKnock
                )
            )
        )
    }

    private fun calcAfCorrectionMonitor(afCorrection: Float): MonitoredNumericSensor {
        // TODO
        return MonitoredNumericSensor(
            value = afCorrection,
            status = NumericStatus.OK,
            summary = Summary(
                minSession = min(
                    previousMonitoredSensorData.afCorrection.summary.minSession,
                    afCorrection
                ),
                maxSession = max(
                    previousMonitoredSensorData.afCorrection.summary.maxSession,
                    afCorrection
                )
            )
        )
    }

    private fun calcAfLearnMonitor(afLearn: Float): MonitoredNumericSensor {
        return MonitoredNumericSensor(
            value = afLearn,
            status = when {
                abs(afLearn) < 8 -> NumericStatus.OK
                abs(afLearn) < 10 -> NumericStatus.WARN
                else -> NumericStatus.CRITICAL
            },
            summary = Summary(
                minSession = min(previousMonitoredSensorData.afLearn.summary.minSession, afLearn),
                maxSession = max(previousMonitoredSensorData.afLearn.summary.maxSession, afLearn)
            )
        )
    }

    private fun calcAfrMonitor(afRatio: Float): MonitoredNumericSensor {
        // TODO
        return MonitoredNumericSensor(
            value = afRatio,
            status = NumericStatus.OK,
            summary = Summary(
                minSession = min(previousMonitoredSensorData.afRatio.summary.minSession, afRatio),
                maxSession = max(previousMonitoredSensorData.afRatio.summary.maxSession, afRatio)
            )
        )
    }

    fun startMonitoring() {
        dataCollectionJob = CoroutineScope(Dispatchers.IO).launch {
            dataRepository.getSensorDataStream().collect { event ->
                val monitoredSensorData = MonitoredSensorData(
                    coolantTemp = calcCoolantTempMonitor(event.coolantTemp),
                    oilTemp = calcOilTempMonitor(event.oilTemp),
                    oilPressure = calcOilPressureMonitor(event.oilPressure, event.engineRpm),
                    fuelPressure = calcFuelPressureMonitor(event.fuelPressure, event.engineRpm),
                    boostPressure = calcBoostPressureMonitor(event.boostPressure),
                    dynamicAdvanceMultiplier = calcDamMonitor(event.dynamicAdvanceMultiplier),
                    fineKnockLearn = calcFineKnockLearnMonitor(event.fineKnockLearn),
                    feedbackKnock = calcFeedbackKnockMonitor(event.feedbackKnock),
                    afCorrection = calcAfCorrectionMonitor(event.afCorrection),
                    afLearn = calcAfLearnMonitor(event.afLearn),
                    afRatio = calcAfrMonitor(event.afRatio),

                    engineRpm = SimpleSensor(event.engineRpm),
                    engineLoad = SimpleSensor(event.engineLoad),
                    throttlePosition = SimpleSensor(event.throttlePosition),
                    ethanolContent = SimpleSensor(event.ethanolContent),
                    intakeAirTemp = SimpleSensor(event.intakeAirTemp)
                )
                previousMonitoredSensorData = monitoredSensorData

                _monitored.emit(monitoredSensorData)
            }
        }
    }

    fun stopMonitoring() {
        dataCollectionJob?.cancel()
    }

    fun reset() {
        previousMonitoredSensorData = MonitoredSensorData()
    }
}
