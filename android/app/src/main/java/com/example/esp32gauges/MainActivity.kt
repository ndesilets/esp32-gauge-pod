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

    Column {
        Text(text="oil pressure: ${uiState.value.sensorData?.oilPressure ?: 0}")
        Text(text="oil temperature: ${uiState.value.sensorData?.oilTemperature ?: 0}")
        Text(text="cooltant temperature: ${uiState.value.sensorData?.coolantTemperature ?: 0}")
        Text(text="boost psi: ${uiState.value.sensorData?.boostPsi ?: 0}")
        Text(text="dynamic advance multiplier: ${uiState.value.sensorData?.dynamicAdvanceMultiplier ?: 0}")
        Text(text="fine knock learn: ${uiState.value.sensorData?.fineKnock ?: 0}")
        Text(text="feedback knock: ${uiState.value.sensorData?.feedbackKnock ?: 0}")
        Text(text="af long term trim: ${uiState.value.sensorData?.afLearn ?: 0}")

        Text(text="engine rpm: ${uiState.value.sensorData?.engineRpm ?: 0}")
        Text(text="engine load: ${uiState.value.sensorData?.engineLoad ?: 0}")
        Text(text="throttle position: ${uiState.value.sensorData?.throttlePosition ?: 0}")
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