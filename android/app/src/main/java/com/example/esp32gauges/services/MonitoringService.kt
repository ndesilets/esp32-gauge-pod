package com.example.esp32gauges.services

import com.example.esp32gauges.models.MonitoredSensorData
import com.example.esp32gauges.models.sensors.MonitoredNumericSensor
import com.example.esp32gauges.models.sensors.MonitoredPressureSensor
import com.example.esp32gauges.models.sensors.MonitoredTempSensor
import com.example.esp32gauges.models.sensors.SimpleSensor
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

data class SensorStatuses(
    val oilPressure: PressureStatus,
    val oilTemp: TempStatus,
    val coolantTemp: TempStatus,
    val fuelPressure: PressureStatus,
    val boostPressure: PressureStatus,
    val dam: NumericStatus,
    val fineKnock: NumericStatus,
    val feedbackKnock: NumericStatus,
    val afCorrection: NumericStatus,
    val afLearn: NumericStatus,
    val afRatio: NumericStatus
)

class MonitoringService(val dataRepository: SensorDataRepository) {
    val dataStream = dataRepository.getSensorDataStream()

    private val _monitored = MutableSharedFlow<MonitoredSensorData>()
    val monitored = _monitored.asSharedFlow()

    private var dataCollectionJob: Job? = null

    private fun calcCoolantTempStatus(temp: Float): TempStatus {
        return when {
            temp < 170 -> TempStatus.COLD
            temp < 220 -> TempStatus.OK
            temp < 230 -> TempStatus.HOT
            else -> TempStatus.CRITICAL
        }
    }

    private fun calcOilPressureStatus(psi: Float, engineRpm: Float): PressureStatus {
        return when {
            engineRpm < 500 -> PressureStatus.OK // ignore if engine is off
            psi < 10 -> PressureStatus.CRITICAL
            else -> PressureStatus.OK
        }
    }

    private fun calcOilTempStatus(temp: Float): TempStatus {
        return when {
            temp < 170 -> TempStatus.COLD
            temp < 240 -> TempStatus.OK
            temp < 250 -> TempStatus.HOT
            else -> TempStatus.CRITICAL
        }
    }

    private fun calCoolantTempStatus(temp: Float): TempStatus {
        return when {
            temp < 170 -> TempStatus.COLD
            temp < 220 -> TempStatus.OK
            temp < 230 -> TempStatus.HOT
            else -> TempStatus.CRITICAL
        }
    }

    private fun calcFuelPressureStatus(psi: Float, engineRpm: Float): PressureStatus {
        return when {
            engineRpm < 500 -> PressureStatus.OK // ignore if engine is off
            psi < 30 -> PressureStatus.CRITICAL
            else -> PressureStatus.OK
        }
    }

    private fun calcBoostPressureStatus(psi: Float): PressureStatus {
        return when {
            psi < 20.5 -> PressureStatus.OK
            psi < 21.5 -> PressureStatus.HIGH
            else -> PressureStatus.CRITICAL // excess boost creep
        }
    }

    private fun calcDamStatus(dam: Float): NumericStatus {
        return when {
            dam == 1.0f -> NumericStatus.OK
            else -> NumericStatus.CRITICAL
        }
    }

    private fun calcFineKnockLearnStatus(fineKnockLearn: Float): NumericStatus {
        return when {
            fineKnockLearn < -4.2 -> NumericStatus.CRITICAL
            else -> NumericStatus.OK
        }
    }

    private fun calcFeedbackKnockStatus(feedbackKnock: Float): NumericStatus {
        return when {
            feedbackKnock < -2.8 -> NumericStatus.CRITICAL
            feedbackKnock < 0 -> NumericStatus.WARN
            else -> NumericStatus.OK
        }
    }

    private fun calcAfCorrectionStatus(): NumericStatus {
        // TODO
        return NumericStatus.OK
    }

    private fun calcAfLearnStatus(afLearn: Float): NumericStatus {
        return when {
            abs(afLearn) < 8 -> NumericStatus.OK
            abs(afLearn) < 10 -> NumericStatus.WARN
            else -> NumericStatus.CRITICAL
        }
    }

    private fun calcAfrStatus(afr: Float): NumericStatus {
        // TODO
        return NumericStatus.OK
    }

    fun startMonitoring() {
        dataCollectionJob = CoroutineScope(Dispatchers.IO).launch {
            dataRepository.getSensorDataStream().collect { event ->
                // do something with the data
                val monitoredSensorData = MonitoredSensorData(
                    coolantTemp = MonitoredTempSensor(event.coolantTemp, calcCoolantTempStatus(event.coolantTemp)),
                    oilTemp = MonitoredTempSensor(event.oilTemp, calcOilTempStatus(event.oilTemp)),
                    oilPressure = MonitoredPressureSensor(event.oilPressure, calcOilPressureStatus(event.oilPressure, event.engineRpm)),
                    fuelPressure = MonitoredPressureSensor(event.fuelPressure, calcFuelPressureStatus(event.fuelPressure, event.engineRpm)),
                    boostPressure = MonitoredPressureSensor(event.boostPressure, calcBoostPressureStatus(event.boostPressure)),
                    dynamicAdvanceMultiplier = MonitoredNumericSensor(event.dynamicAdvanceMultiplier, calcDamStatus(event.dynamicAdvanceMultiplier)),
                    fineKnock = MonitoredNumericSensor(event.fineKnockLearn, calcFineKnockLearnStatus(event.fineKnockLearn)),
                    feedbackKnock = MonitoredNumericSensor(event.feedbackKnock, calcFeedbackKnockStatus(event.feedbackKnock)),
                    afCorrection = MonitoredNumericSensor(event.afCorrection, calcAfCorrectionStatus()),
                    afLearn = MonitoredNumericSensor(event.afLearn, calcAfLearnStatus(event.afLearn)),
                    afRatio = MonitoredNumericSensor(event.afRatio, calcAfrStatus(event.afRatio)),

                    engineRpm = SimpleSensor(event.engineRpm),
                    engineLoad = SimpleSensor(event.engineLoad),
                    throttlePosition = SimpleSensor(event.throttlePosition),
                    ethanolContent = SimpleSensor(event.ethanolContent)
                )

                _monitored.emit(monitoredSensorData)
            }
        }
    }

    fun stopMonitoring() {
        dataCollectionJob?.cancel()
    }
}
