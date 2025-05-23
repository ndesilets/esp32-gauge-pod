package com.example.esp32gauges.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esp32gauges.MainViewModel
import com.example.esp32gauges.composables.AirFuelFunLand
import com.example.esp32gauges.composables.BarGauge
import com.example.esp32gauges.composables.KnockFunLand
import com.example.esp32gauges.composables.SensorStatusBar
import com.example.esp32gauges.composables.SimpleNumericGauge
import com.example.esp32gauges.repositories.daos.MockSensorDataEventDao
import com.example.esp32gauges.esp32.MockedESP32DataSource
import com.example.esp32gauges.repositories.SensorDataRepository
import com.example.esp32gauges.services.MonitoringService
import com.example.esp32gauges.ui.theme.ESP32GaugesTheme

private val WHITE = Color(0xFFFFFFFF)

@Composable
fun Dashboard(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val uiState = viewModel.uiState.collectAsState()
    val sensors = uiState.value.monitoredSensorData

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        SensorStatusBar(
            sensors, modifier = Modifier
                .fillMaxWidth()
        )

        HorizontalDivider(modifier.padding(vertical = 16.dp))

        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BarGauge(
                name = "Coolant Temp",
                minVal = -20f,
                maxVal = 300f,
                currentVal = sensors.coolantTemp.value,
                detents = listOf(-20f, 0f, 32f, 100f, 180f, 230f, 300f),
                modifier
            )
        }

        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BarGauge(
                name = "Oil Temp",
                minVal = -20f,
                maxVal = 300f,
                currentVal = sensors.oilTemp.value,
                detents = listOf(-20f, 0f, 32f, 100f, 180f, 250f, 300f),
                modifier
            )
        }

        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BarGauge(
                name = "Oil Pressure",
                minVal = 0f,
                maxVal = 100f,
                currentVal = sensors.oilPressure.value,
                detents = listOf(0f, 10f, 20f, 40f, 60f, 80f, 100f),
                modifier
            )
        }

        HorizontalDivider(modifier.padding(vertical = 16.dp))

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                SimpleNumericGauge(
                    title = "IAT",
                    curVal = sensors.intakeAirTemp.value,
                    modifier = modifier.weight(1f)
                )
                SimpleNumericGauge(
                    title = "ETH",
                    curVal = sensors.ethanolContent.value,
                    modifier = modifier.weight(1f)
                )
                SimpleNumericGauge(
                    title = "FUEL P",
                    curVal = sensors.fuelPressure.value,
                    modifier = modifier.weight(1f)
                )
            }
        }

        Box(modifier = modifier.padding(vertical = 8.dp)){
            AirFuelFunLand(
                afCorrection = sensors.afCorrection,
                afLearn = sensors.afLearn,
                afRatio = sensors.afRatio,
            )
        }

        Box(modifier = modifier.padding(vertical = 8.dp)){
            KnockFunLand(
                dam = sensors.afCorrection,
                afCorrection = sensors.afCorrection,
                afLearn = sensors.afLearn,
            )
        }
    }
}

@Preview(widthDp = 480, heightDp = 900, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DashboardPreview() {
    val viewModel =
        MainViewModel(MonitoringService( SensorDataRepository(MockedESP32DataSource(), MockSensorDataEventDao())))

    ESP32GaugesTheme {
        Surface {
            Dashboard(viewModel)
        }
    }
}