package com.example.esp32gauges

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.esp32gauges.esp32.MockedESP32DataSource
import com.example.esp32gauges.repositories.SensorDataRepository
import com.example.esp32gauges.repositories.SensorDatabase
import com.example.esp32gauges.services.MonitoringService
import com.example.esp32gauges.ui.theme.ESP32GaugesTheme
import com.example.esp32gauges.views.Dashboard

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Dashboard2 : Screen("dashboard")
    object Dashboard3 : Screen("dashboard")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sensorDataSource = MockedESP32DataSource()
        val sensorDatabase =
            Room.inMemoryDatabaseBuilder(applicationContext, SensorDatabase::class.java).build()

        val dataRepository = SensorDataRepository(sensorDataSource, sensorDatabase.sensorDataEventDao())
        val monitoringService = MonitoringService(dataRepository)
        monitoringService.startMonitoring()

        val viewModel = MainViewModel(monitoringService)

        setContent {
            ESP32GaugesTheme {
                Surface {
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
    val currentScreen = listOf(Screen.Dashboard, Screen.Dashboard2, Screen.Dashboard3).find {
        it.route == currentDestination?.route
    } ?: Screen.Dashboard
}