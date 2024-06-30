package com.example.esp32gauges

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.esp32gauges.daos.MockSensorDataEventDao
import com.example.esp32gauges.esp32.MockedESP32DataSource
import com.example.esp32gauges.repositories.SensorDataRepository
import com.example.esp32gauges.repositories.SensorDatabase
import com.example.esp32gauges.sensors.status.NumericStatus
import com.example.esp32gauges.sensors.status.PressureStatus
import com.example.esp32gauges.sensors.status.TempStatus
import com.example.esp32gauges.ui.theme.ESP32GaugesTheme
import com.example.esp32gauges.views.Dashboard
import com.example.esp32gauges.views.SensorStatusBar

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Dashboard2 : Screen("dashboard")
    object Dashboard3 : Screen("dashboard")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataSource = MockedESP32DataSource()
        val sensorDatabase = Room.inMemoryDatabaseBuilder(applicationContext, SensorDatabase::class.java).build()
        val dataRepository = SensorDataRepository(dataSource, sensorDatabase.sensorDataEventDao())
        val viewModel = MainViewModel(dataRepository)

        setContent {
            ESP32GaugesTheme {
                Surface() {
                    Dashboard(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppContent() {
    var navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = listOf(Screen.Dashboard, Screen.Dashboard2, Screen.Dashboard3) .find {
        it.route == currentDestination?.route
    } ?: Screen.Dashboard
}