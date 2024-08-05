package com.example.esp32gauges.composables

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.esp32gauges.MainViewModel
import com.example.esp32gauges.repositories.daos.MockSensorDataEventDao
import com.example.esp32gauges.esp32.MockedESP32DataSource
import com.example.esp32gauges.models.MonitoredSensorData
import com.example.esp32gauges.repositories.SensorDataRepository
import com.example.esp32gauges.models.sensors.status.NumericStatus
import com.example.esp32gauges.models.sensors.status.PressureStatus
import com.example.esp32gauges.models.sensors.status.TempStatus
import com.example.esp32gauges.services.MonitoringService
import com.example.esp32gauges.ui.theme.ESP32GaugesTheme

@Composable
fun StatusIndicator(name: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .padding(4.dp)
            .height(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = name, textAlign = TextAlign.Center)
        Box(
            modifier
                .height(4.dp)
                .background(color, shape = RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun PressureSensorStatus(name: String, status: PressureStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        PressureStatus.LOW -> Color.Yellow
        PressureStatus.OK -> Color.Green
        PressureStatus.HIGH -> Color.Yellow
        else -> Color.Red
    }

    StatusIndicator(name, color, modifier)
}

@Composable
fun TempSensorStatus(name: String, status: TempStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        TempStatus.COLD -> Color.Blue
        TempStatus.OK -> Color.Green
        TempStatus.HOT -> Color.Yellow
        else -> Color.Red
    }

    StatusIndicator(name, color, modifier)
}

@Composable
fun NumericSensorStatus(name: String, status: NumericStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        NumericStatus.OK -> Color.Green
        NumericStatus.WARN -> Color.Yellow
        else -> Color.Red
    }

    StatusIndicator(name, color, modifier)
}

//

@Composable
fun SensorStatusBar(sensors: MonitoredSensorData, modifier: Modifier = Modifier) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            TempSensorStatus(
                name = "Coolant Temp",
                status = sensors.coolantTemp.status,
                modifier.weight(1f)
            )
            TempSensorStatus(
                name = "Oil Temp",
                status = sensors.oilTemp.status,
                modifier.weight(1f)
            )
            PressureSensorStatus(
                name = "Oil PSI",
                status = sensors.oilPressure.status,
                modifier.weight(1f)
            )
            PressureSensorStatus(
                name = "Fuel PSI",
                status = sensors.fuelPressure.status,
                modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            NumericSensorStatus(
                name = "DAM",
                status = sensors.dynamicAdvanceMultiplier.status,
                modifier.weight(1f)
            )
            NumericSensorStatus(
                name = "FB Knock",
                status = sensors.feedbackKnock.status,
                modifier.weight(1f)
            )
            PressureSensorStatus(
                name = "Boost PSI",
                status = sensors.boostPressure.status,
                modifier.weight(1f)
            )
            NumericSensorStatus(
                name = "AF Ratio",
                status = sensors.afRatio.status,
                modifier.weight(1f)
            )
        }
    }
}

@Preview(heightDp = 120, widthDp = 480, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SensorIndicatorOverviewPreview() {
    val viewModel =
        MainViewModel(MonitoringService( SensorDataRepository(MockedESP32DataSource(), MockSensorDataEventDao())))
    val uiState = viewModel.uiState.collectAsState()
    val sensors = uiState.value.monitoredSensorData

    ESP32GaugesTheme {
        Surface {
            SensorStatusBar(sensors)
        }
    }
}