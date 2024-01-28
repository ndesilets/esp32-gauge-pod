package com.example.esp32gauges

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.esp32gauges.sensors.NumericStatus
import com.example.esp32gauges.sensors.PressureStatus
import com.example.esp32gauges.sensors.TempStatus
import com.example.esp32gauges.ui.theme.ESP32GaugesTheme

class MainActivity : ComponentActivity() {
    val viewModel = MainViewModel(SensorDataRepository(MockedESP32DataSource()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            ESP32GaugesTheme {
                Surface {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        SensorIndicatorOverview(
                            viewModel, modifier = Modifier
                                .height(108.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SensorIndicatorOverview(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val uiState = viewModel.uiState.collectAsState()
    val sensors = uiState.value.monitoredSensorData

//    Log.d("sensors: %s", sensors.toString())

    Surface(
        modifier.padding(4.dp)
            .border(BorderStroke(1.dp, Color.White), shape = RoundedCornerShape(8.dp))
    ) {
        Column(modifier) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                PressureSensorStatus(
                    name = "Oil PSI",
                    status = sensors.oilPressure.status,
                    modifier.weight(1f)
                )
                TempSensorStatus(
                    name = "Oil T",
                    status = sensors.oilTemp.status,
                    modifier.weight(1f)
                )
                TempSensorStatus(
                    name = "Coolant T",
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
                    .weight(1f)
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

@Composable
fun StatusIndicator(name: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = name)
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

@Preview(showBackground = true, heightDp = 120)
@Composable
fun SensorsOverviewPreview() {
    ESP32GaugesTheme {
        SensorIndicatorOverview(MainViewModel(SensorDataRepository(MockedESP32DataSource())))
    }
}

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