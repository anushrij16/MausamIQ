package com.aetherx.mausamiq.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    DASHBOARD("dashboard", "Dashboard", Icons.Rounded.Home),
    FORECAST("forecast", "Forecast", Icons.Rounded.CalendarMonth),
    INSIGHTS("insights", "AI Insights", Icons.Rounded.Psychology),
    MAP("map", "Radar Map", Icons.Rounded.Map),
    TRAVEL("travel", "Smart Travel", Icons.Rounded.Explore),
    ALERTS("alerts", "Alerts", Icons.Rounded.Notifications),
    PROFILE("profile", "Profile", Icons.Rounded.Person),
    SETTINGS("settings", "Settings", Icons.Rounded.Settings);

    companion object {
        val bottomNavDestinations = listOf(
            DASHBOARD,
            FORECAST,
            INSIGHTS,
            MAP,
            TRAVEL,
            ALERTS
        )
    }
}
