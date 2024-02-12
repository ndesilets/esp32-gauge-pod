package com.example.esp32gauges

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esp32gauges.composables.BarGauge
import com.example.esp32gauges.esp32.MockedESP32DataSource
import com.example.esp32gauges.esp32.SensorDataRepository
import com.example.esp32gauges.models.MonitoredSensorData
import com.example.esp32gauges.sensors.status.NumericStatus
import com.example.esp32gauges.sensors.status.PressureStatus
import com.example.esp32gauges.sensors.status.TempStatus
import com.example.esp32gauges.ui.theme.ESP32GaugesTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val viewModel = MainViewModel(SensorDataRepository(MockedESP32DataSource()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ESP32GaugesTheme {
                Surface() {
                    Dashboard(viewModel)
                }
            }
        }
    }
}

//

@Composable
fun Dashboard(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val uiState = viewModel.uiState.collectAsState()
    val sensors = uiState.value.monitoredSensorData

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        SensorIndicatorOverview(
            sensors, modifier = Modifier
                .fillMaxWidth()
        )

        Divider(modifier.padding(vertical = 32.dp))

        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Coolant Temp")
            Text(
                "${sensors.coolantTemp.value.toInt()}",
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            BarGauge(
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
            Text("Oil Temp")
            Text(
                "${sensors.oilTemp.value.toInt()}",
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            BarGauge(
                minVal = -20f,
                maxVal = 300f,
                currentVal = sensors.oilTemp.value,
                detents = listOf(-20f, 0f, 32f, 100f, 180f, 230f, 300f),
                modifier
            )
        }

        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Oil Pressure")
            Text(
                "${sensors.oilPressure.value.toInt()}",
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            BarGauge(
                minVal = 0f,
                maxVal = 100f,
                currentVal = sensors.oilPressure.value,
                detents = listOf(0f, 10f, 20f, 40f, 60f, 80f, 100f),
                modifier
            )
        }

        Divider(modifier.padding(vertical = 32.dp))

        Row(
            modifier
                .height(80.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Dyn Adv Multiplier")
                Text(
                    String.format(Locale.US, "%.1f", sensors.dynamicAdvanceMultiplier.value),
                    fontSize = 48.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Fine Knock Learn")
                Text(
                    String.format(Locale.US, "%.1f", sensors.fineKnock.value),
                    fontSize = 48.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier
                .height(80.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Feedback Knock")
                Text(
                    String.format(Locale.US, "%.1f", sensors.feedbackKnock.value),
                    fontSize = 48.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("AF Long Term Learn")
                Text(
                    String.format(Locale.US, "%.1f", sensors.afLearn.value),
                    fontSize = 48.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(widthDp = 480, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DashboardPreview() {
    val viewModel = MainViewModel(SensorDataRepository(MockedESP32DataSource()))

    ESP32GaugesTheme {
        Surface() {
            Dashboard(viewModel)
        }
    }
}

//

@Composable
fun SensorIndicatorOverview(sensors: MonitoredSensorData, modifier: Modifier = Modifier) {
//    Log.d("sensors: %s", sensors.toString())

    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
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

@Preview(heightDp = 120, widthDp = 480, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SensorIndicatorOverviewPreview() {
    val viewModel = MainViewModel(SensorDataRepository(MockedESP32DataSource()))
    val uiState = viewModel.uiState.collectAsState()
    val sensors = uiState.value.monitoredSensorData

    ESP32GaugesTheme {
        Surface() {
            SensorIndicatorOverview(sensors)
        }
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