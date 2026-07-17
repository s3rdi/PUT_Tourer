package com.example.tourer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tourer.ui.ActiveTimerCard
import com.example.tourer.ui.RouteDetailScreen
import com.example.tourer.ui.RouteListScreen
import com.example.tourer.ui.WelcomeScreen
import com.example.tourer.viewmodel.RouteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = getSharedPreferences("tourer_prefs", MODE_PRIVATE)
        val savedTheme = sharedPreferences.getBoolean("is_dark_theme", true)
        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(savedTheme) }
            var hasStarted by rememberSaveable { mutableStateOf(false) }

            val bgColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF2F5FA)
            val surfaceColor = if (isDarkTheme) Color(0xFF242424) else Color(0xFFFFFFFF)
            val textColor = if (isDarkTheme) Color.White else Color.Black
            val accentColor = if (isDarkTheme) Color.Green else Color(0xFF1EBA12)

            LaunchedEffect(isDarkTheme) {
                sharedPreferences.edit().putBoolean("is_dark_theme", isDarkTheme).apply()
            }

            val navController = rememberNavController()
            val viewModel: RouteViewModel = viewModel()
            val routes by viewModel.routes.collectAsState()

            val configuration = LocalConfiguration.current
            val isExpandedScreen = configuration.screenWidthDp >= 840 && configuration.screenHeightDp >= 480
            val selectedRouteId by viewModel.selectedRouteId.collectAsState()

            val isTimerRunning by viewModel.isTimerRunning.collectAsState()
            val activeRouteName by viewModel.activeTimerRouteName.collectAsState()
            val activeRouteId by viewModel.activeTimerRouteId.collectAsState()
            val timeString by viewModel.formattedTime.collectAsState()
            val time by viewModel.timeInSeconds.collectAsState()

            val showStopConfirm by viewModel.showStopConfirm.collectAsState()
            val isTablet = configuration.screenWidthDp >= 600 && configuration.screenHeightDp >= 480


            LaunchedEffect(selectedRouteId, isExpandedScreen) {
                if (!isExpandedScreen && selectedRouteId != null) {
                    navController.navigate("routeDetail/$selectedRouteId") {
                        launchSingleTop = true
                    }
                }
            }

            if (!hasStarted) {
                WelcomeScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme },
                    onStartClick = { hasStarted = true }
                )
            } else {
                if (isExpandedScreen) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f)) {
                            RouteListScreen(
                                isDarkTheme = isDarkTheme,
                                onThemeToggle = { isDarkTheme = !isDarkTheme },
                                viewModel = viewModel,
                                routes = routes,
                                onRouteClick = { routeId ->
                                    viewModel.selectRoute(routeId)
                                }
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .background(bgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            val selectedRoute = routes.find { it.id.toString() == selectedRouteId }
                            if (selectedRoute != null) {
                                RouteDetailScreen(
                                    isDarkTheme = isDarkTheme,
                                    onThemeToggle = { isDarkTheme = !isDarkTheme },
                                    viewModel = viewModel,
                                    route = selectedRoute,
                                    onBackClick = {
                                        viewModel.selectRoute(null)
                                    }
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = "map icon",
                                        tint = textColor,
                                        modifier = Modifier.size(100.dp)
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Text(
                                        text = "Wybierz trasę i odkryj swoją następną przygodę!",
                                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 32.sp),
                                        color = textColor
                                    )
                                }

                                if (time > 0 || isTimerRunning) {
                                    ActiveTimerCard(
                                        viewModel = viewModel,
                                        timeString = timeString,
                                        activeRouteName = activeRouteName,
                                        activeRouteId = activeRouteId,
                                        isTimerRunning = isTimerRunning,
                                        showStopConfirm = showStopConfirm,
                                        time = time,
                                        surfaceColor = surfaceColor,
                                        textColor = textColor,
                                        bgColor = bgColor,
                                        accentColor = accentColor,
                                        isTablet = isTablet
                                    )
                                }
                            }
                        }
                    }
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = "routeList"
                    ) {
                        composable(route = "routeList") {
                            RouteListScreen(
                                isDarkTheme = isDarkTheme,
                                onThemeToggle = { isDarkTheme = !isDarkTheme },
                                viewModel = viewModel,
                                routes = routes,
                                onRouteClick = { routeId ->
                                    viewModel.selectRoute(routeId)
                                    navController.navigate("routeDetail/$routeId")
                                })
                        }

                        composable(route = "routeDetail/{routeId}") { backStackEntry ->
                            val routeId = backStackEntry.arguments?.getString("routeId")
                            val selectedRoute = routes.find { it.id.toString() == routeId }
                            RouteDetailScreen(
                                isDarkTheme = isDarkTheme,
                                onThemeToggle = { isDarkTheme = !isDarkTheme },
                                viewModel = viewModel,
                                route = selectedRoute,
                                onBackClick = {
                                    viewModel.selectRoute(null)
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}