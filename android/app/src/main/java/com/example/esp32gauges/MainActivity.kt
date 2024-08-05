package com.example.esp32gauges

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.esp32gauges.esp32.MockedESP32DataSource
import com.example.esp32gauges.repositories.SensorDataRepository
import com.example.esp32gauges.repositories.SensorDatabase
import com.example.esp32gauges.services.MonitoringService
import com.example.esp32gauges.ui.theme.ESP32GaugesTheme
import com.example.esp32gauges.views.Dashboard
import com.example.esp32gauges.views.SessionManager


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
                val navController = rememberNavController()

                Scaffold(bottomBar = { BottomNavigationBar(navController) }) { innerPadding ->
                    NavHost(navController, startDestination = Screen.Dashboard.route, modifier = Modifier.padding(innerPadding)) {
                        composable(Screen.Dashboard.route) { Dashboard(viewModel) }
                        composable(Screen.SessionManager.route) { SessionManager() }
                    }
                }
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object SessionManager : Screen("session-manager", "Session", Icons.Default.Settings)
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Dashboard,
        Screen.SessionManager
    )
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun AppContent(viewModel: MainViewModel) {

}