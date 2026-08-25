package com.aetherx.mausamiq.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aetherx.mausamiq.presentation.alerts.AlertsScreen
import com.aetherx.mausamiq.presentation.alerts.AlertsViewModel
import com.aetherx.mausamiq.presentation.dashboard.DashboardScreen
import com.aetherx.mausamiq.presentation.dashboard.DashboardViewModel
import com.aetherx.mausamiq.presentation.forecast.ForecastScreen
import com.aetherx.mausamiq.presentation.forecast.ForecastViewModel
import com.aetherx.mausamiq.presentation.insights.AiInsightsScreen
import com.aetherx.mausamiq.presentation.insights.InsightsViewModel
import com.aetherx.mausamiq.presentation.map.WeatherMapScreen
import com.aetherx.mausamiq.presentation.map.WeatherMapViewModel
import com.aetherx.mausamiq.presentation.profile.ProfileScreen
import com.aetherx.mausamiq.presentation.profile.ProfileViewModel
import com.aetherx.mausamiq.presentation.settings.SettingsScreen
import com.aetherx.mausamiq.presentation.settings.SettingsViewModel
import com.aetherx.mausamiq.presentation.travel.SmartTravelScreen
import com.aetherx.mausamiq.presentation.travel.SmartTravelViewModel

@Composable
fun MainAppShell(
    dashboardViewModel: DashboardViewModel,
    forecastViewModel: ForecastViewModel,
    insightsViewModel: InsightsViewModel,
    mapViewModel: WeatherMapViewModel,
    travelViewModel: SmartTravelViewModel,
    alertsViewModel: AlertsViewModel,
    profileViewModel: ProfileViewModel,
    settingsViewModel: SettingsViewModel,
    onLogout: () -> Unit
) {
    var currentDestination by remember { mutableStateOf(NavigationDestination.DASHBOARD) }
    val dashboardState by dashboardViewModel.uiState.collectAsState()

    // Android Back button handling: If not on Dashboard, return to Dashboard first
    BackHandler(enabled = currentDestination != NavigationDestination.DASHBOARD) {
        currentDestination = NavigationDestination.DASHBOARD
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 600.dp

        Scaffold(
            topBar = {
                MausamTopAppBar(
                    currentLocationName = dashboardState.weather?.locationName ?: dashboardState.userName,
                    unreadAlertCount = dashboardState.unreadAlertCount,
                    isDemoMode = dashboardState.isDemoMode,
                    onLocationClick = { dashboardViewModel.toggleLocationPicker(true) },
                    onSearchClick = { currentDestination = NavigationDestination.MAP },
                    onNotificationsClick = { currentDestination = NavigationDestination.ALERTS },
                    onProfileClick = { currentDestination = NavigationDestination.PROFILE }
                )
            },
            bottomBar = {
                if (!isTablet) {
                    MausamBottomNavBar(
                        currentDestination = currentDestination,
                        onNavigateTo = { currentDestination = it }
                    )
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isTablet) {
                    MausamNavRail(
                        currentDestination = currentDestination,
                        onNavigateTo = { currentDestination = it }
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    AnimatedContent(
                        targetState = currentDestination,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                        },
                        label = "main_screen_nav"
                    ) { destination ->
                        when (destination) {
                            NavigationDestination.DASHBOARD -> DashboardScreen(
                                viewModel = dashboardViewModel,
                                onNavigateToInsights = { currentDestination = NavigationDestination.INSIGHTS }
                            )
                            NavigationDestination.FORECAST -> ForecastScreen(
                                viewModel = forecastViewModel
                            )
                            NavigationDestination.INSIGHTS -> AiInsightsScreen(
                                viewModel = insightsViewModel
                            )
                            NavigationDestination.MAP -> WeatherMapScreen(
                                viewModel = mapViewModel
                            )
                            NavigationDestination.TRAVEL -> SmartTravelScreen(
                                viewModel = travelViewModel
                            )
                            NavigationDestination.ALERTS -> AlertsScreen(
                                viewModel = alertsViewModel
                            )
                            NavigationDestination.PROFILE -> ProfileScreen(
                                viewModel = profileViewModel
                            )
                            NavigationDestination.SETTINGS -> SettingsScreen(
                                viewModel = settingsViewModel,
                                onLogout = onLogout
                            )
                        }
                    }
                }
            }
        }
    }
}
