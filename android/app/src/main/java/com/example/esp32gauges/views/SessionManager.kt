package com.example.esp32gauges.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.esp32gauges.MainViewModel
import com.example.esp32gauges.esp32.MockedESP32DataSource
import com.example.esp32gauges.repositories.SensorDataRepository
import com.example.esp32gauges.repositories.daos.MockSensorDataEventDao
import com.example.esp32gauges.services.MonitoringService
import com.example.esp32gauges.ui.theme.ESP32GaugesTheme


private val WHITE = Color(0xFFFFFFFF)

@Composable
fun SessionManager(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(fontSize = 24.sp, color = WHITE, text = "lol")
        Text(fontSize = 24.sp, color = WHITE, text = "lmao")
    }
}

@Preview(widthDp = 480, heightDp = 1056, uiMode = Configuration.UI_MODE_NIGHT_YES)
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