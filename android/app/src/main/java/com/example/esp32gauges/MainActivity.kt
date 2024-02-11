package com.example.esp32gauges

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.esp32gauges.composables.BarGauge
import com.example.esp32gauges.esp32.MockedESP32DataSource
import com.example.esp32gauges.esp32.SensorDataRepository
import com.example.esp32gauges.models.MonitoredSensorData
import com.example.esp32gauges.sensors.status.NumericStatus
import com.example.esp32gauges.sensors.status.PressureStatus
import com.example.esp32gauges.sensors.status.TempStatus
import com.example.esp32gauges.ui.theme.ESP32GaugesTheme

class MainActivity : ComponentActivity() {
    val viewModel = MainViewModel(SensorDataRepository(MockedESP32DataSource()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ESP32GaugesTheme {
                Dashboard(viewModel)
            }
        }
    }
}

//

@Composable
fun Dashboard(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val uiState = viewModel.uiState.collectAsState()
    val sensors = uiState.value.monitoredSensorData

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SensorIndicatorOverview(
                sensors, modifier = Modifier
                    .fillMaxWidth()
            )

//            BarGauge(percentage = 0.55f, modifier.size(24.dp, 4.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
fun DashboardPreview() {
    ESP32GaugesTheme {
        Dashboard(viewModel = MainViewModel(SensorDataRepository(MockedESP32DataSource())))
    }
}

//

@Composable
fun SensorIndicatorOverview(sensors: MonitoredSensorData, modifier: Modifier = Modifier) {
//    Log.d("sensors: %s", sensors.toString())

    Surface(
        modifier
            .padding(4.dp)
            .border(BorderStroke(1.dp, Color.White), shape = RoundedCornerShape(8.dp))
    ) {
        Column(modifier) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PressureSensorStatus(
                    name = "Oil PSI",
                    status = sensors.oilPressure.status,
                    modifier.weight(1f)
                )
                TempSensorStatus(
                    name = "Oil Temp",
                    status = sensors.oilTemp.status,
                    modifier.weight(1f)
                )
                TempSensorStatus(
                    name = "Coolant Temp",
                    status = sensors.coolantTemp.status,
                    modifier.weight(1f)
                )
                PressureSensorStatus(
                    name = "Boost PSI",
                    status = sensors.boostPressure.status,
                    modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                NumericSensorStatus(
                    name = "DAM",
                    status = sensors.dynamicAdvanceMultiplier.status,
                    modifier.weight(1f)
                )
                NumericSensorStatus(
                    name = "FN Knock",
                    status = sensors.dynamicAdvanceMultiplier.status,
                    modifier.weight(1f)
                )
                NumericSensorStatus(
                    name = "FB Knock",
                    status = sensors.dynamicAdvanceMultiplier.status,
                    modifier.weight(1f)
                )
                NumericSensorStatus(
                    name = "AF Learn",
                    status = sensors.dynamicAdvanceMultiplier.status,
                    modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(heightDp = 120, widthDp = 480)
@Composable
fun SensorIndicatorOverviewPreview() {
    ESP32GaugesTheme {
        SensorIndicatorOverview(MonitoredSensorData())
    }
}

//

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
    val color = when {
        status == PressureStatus.LOW -> Color.Yellow
        status == PressureStatus.OK -> Color.Green
        status == PressureStatus.HIGH -> Color.Yellow
        else -> Color.Red
    }

    StatusIndicator(name, color, modifier)
}

@Composable
fun TempSensorStatus(name: String, status: TempStatus, modifier: Modifier = Modifier) {
    val color = when {
        status == TempStatus.COLD -> Color.Blue
        status == TempStatus.OK -> Color.Green
        status == TempStatus.HOT -> Color.Yellow
        else -> Color.Red
    }

    StatusIndicator(name, color, modifier)
}

@Composable
fun NumericSensorStatus(name: String, status: NumericStatus, modifier: Modifier = Modifier) {
    val color = when {
        status == NumericStatus.OK -> Color.Green
        status == NumericStatus.WARN -> Color.Yellow
        else -> Color.Red
    }

    StatusIndicator(name, color, modifier)
}

//

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ESP32GaugesTheme {
        Greeting("Android")
    }
}