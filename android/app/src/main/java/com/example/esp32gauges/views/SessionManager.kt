package com.example.esp32gauges.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.esp32gauges.MainViewModel
import com.example.esp32gauges.esp32.MockedESP32DataSource
import com.example.esp32gauges.repositories.SensorDataRepository
import com.example.esp32gauges.repositories.daos.MockSensorDataEventDao
import com.example.esp32gauges.services.MonitoringService
import com.example.esp32gauges.ui.theme.ESP32GaugesTheme


@Composable
fun SessionManager(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Button(onClick = { viewModel.reset() }, modifier = modifier) {
            Text("Reset Session")
        }
    }
}

@Preview(widthDp = 480, heightDp = 900, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SessionManagerPreview() {
    val viewModel =
        MainViewModel(MonitoringService( SensorDataRepository(MockedESP32DataSource(), MockSensorDataEventDao())))

    ESP32GaugesTheme {
        Surface {
            SessionManager(viewModel)
        }
    }
}