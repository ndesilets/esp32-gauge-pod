package com.example.esp32gauges

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.esp32gauges.ui.theme.ESP32GaugesTheme

class MainActivity : ComponentActivity() {
    val viewModel = MainViewModel(SensorDataRepository(MockedESP32DataSource()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESP32GaugesTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SensorsList(viewModel)
                }
            }
        }
    }
}

@Composable
fun SensorsList(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val uiState = viewModel.uiState.collectAsState()
    val sensors = uiState.value.monitoredSensorData

    Column {
        Text(text="oil pressure: ${sensors.oilPressure.value}")
        Text(text="oil pressure status: ${sensors.oilPressure.status}")

        Text(text="oil temp: ${sensors.oilTemp.value}")
        Text(text="oil temp status: ${sensors.oilTemp.status}")

        Text(text="coolant temp: ${sensors.coolantTemp.value}")
        Text(text="coolant temp status: ${sensors.coolantTemp.status}")

        Text(text="boost psi: ${sensors.boostPressure.value}")
        Text(text="boost psi status: ${sensors.boostPressure.status}")

        Text(text="dynamic advance multiplier: ${sensors.dynamicAdvanceMultiplier.value}")
        Text(text="dynamic advance multiplier status: ${sensors.dynamicAdvanceMultiplier.status}")

        Text(text="fine knock learn: ${sensors.fineKnock.value}")
        Text(text="fine knock learn status: ${sensors.fineKnock.status}")

        Text(text="feedback knock: ${sensors.feedbackKnock.value}")
        Text(text="feedback knock status: ${sensors.feedbackKnock.status}")

        Text(text="af long term trim: ${sensors.afLearn.value}")
        Text(text="af long term trim status: ${sensors.afLearn.status}")

        Text(text="engine rpm: ${sensors.engineRpm.value}")
        Text(text="engine load: ${sensors.engineLoad.value}")
        Text(text="throttle position: ${sensors.throttlePosition.value}")
    }
}

@Preview(showBackground = true)
@Composable
fun SensorsListPreview() {
    ESP32GaugesTheme {
        SensorsList(MainViewModel(SensorDataRepository(MockedESP32DataSource())))
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