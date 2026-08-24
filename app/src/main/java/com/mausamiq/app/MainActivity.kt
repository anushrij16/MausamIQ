package com.mausamiq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mausamiq.app.domain.AppUiState
import com.mausamiq.app.domain.Persona
import com.mausamiq.app.domain.SavedPlace
import com.mausamiq.app.domain.UserProfile
import com.mausamiq.app.domain.WeatherAlert
import com.mausamiq.app.domain.WeatherSnapshot
import com.mausamiq.app.domain.freshnessText
import com.mausamiq.app.domain.nowLabel
import com.mausamiq.app.domain.primaryPersona
import com.mausamiq.app.ui.AppViewModel
import com.mausamiq.app.ui.theme.MausamColors
import com.mausamiq.app.ui.theme.MausamIQTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MausamIQTheme { MausamIQApp() } }
    }
}

@Composable
fun MausamIQApp(viewModel: AppViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {
                val items = listOf("Home", "Alerts", "Places", "Profile")
                val icons = listOf(Icons.Default.Home, Icons.Default.Notifications, Icons.Default.LocationOn, Icons.Default.Person)
                items.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = state.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        icon = { Icon(icons[index], contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (state.selectedTab) {
                0 -> HomeScreen(state, viewModel)
                1 -> AlertsScreen(state, viewModel)
                2 -> PlacesScreen(state)
                else -> ProfileScreen(state, viewModel)
            }
        }
    }
    if (state.showExplanation) ExplanationDialog(state, viewModel)
    if (state.showTrace) DecisionTraceDialog(state, viewModel)
}

@Composable
private fun HomeScreen(state: AppUiState, viewModel: AppViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Good morning, ${state.profile.name}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Your weather, made relevant", style = MaterialTheme.typography.headlineSmall)
                }
                Surface(shape = CircleShape, color = MausamColors.sky) {
                    IconButton(onClick = { viewModel.refreshDemo() }) { Icon(Icons.Default.Refresh, "Refresh") }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = RoundedCornerShape(50), color = Color(0xFFFFE7B3)) { Text("DEMO MODE", modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFF7A4A00), fontWeight = FontWeight.Bold) }
                Surface(shape = RoundedCornerShape(50), color = Color(0xFFE2F5EC)) { Text("${state.profile.primaryPersona().label} context", modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFF167047)) }
            }
        }
        item { CurrentWeatherCard(state.weather, state.isOffline) }
        item { RecommendationCard(state, viewModel) }
        item { RiskCard(state) }
        item { ComparisonCard(state) }
        item { PrioritiesCard(state) }
        item {
            Text("Built for decisions, not just forecasts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 18.dp))
        }
    }
}

@Composable
private fun CurrentWeatherCard(weather: WeatherSnapshot, offline: Boolean) {
    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MausamColors.blue)) {
        Column(Modifier.padding(22.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.White.copy(alpha = .85f), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(weather.city, color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                    Text("${weather.temperature}°", color = Color.White, style = MaterialTheme.typography.displaySmall)
                    Text(weather.condition, color = Color.White.copy(alpha = .88f))
                }
                Icon(Icons.Default.Cloud, null, tint = Color.White.copy(alpha = .85f), modifier = Modifier.size(58.dp))
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                WeatherStat("Feels like", "${weather.feelsLike}°", Icons.Default.Thermostat)
                WeatherStat("Rain", "${weather.rainChance}%", Icons.Default.WaterDrop)
                WeatherStat("Wind", "${weather.windKph} km/h", Icons.Default.Air)
                WeatherStat("Visibility", "${weather.visibilityKm} km", Icons.Default.Visibility)
            }
            Spacer(Modifier.height(14.dp))
            Text(freshnessText(weather.updatedMinutesAgo, offline), color = Color.White.copy(alpha = .78f), style = MaterialTheme.typography.labelSmall)
            Text("Source: ${weather.source}", color = Color.White.copy(alpha = .65f), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun WeatherStat(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Color.White.copy(alpha = .75f), modifier = Modifier.size(16.dp))
        Text(value, color = Color.White, style = MaterialTheme.typography.labelLarge)
        Text(label, color = Color.White.copy(alpha = .7f), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun RecommendationCard(state: AppUiState, viewModel: AppViewModel) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = Color(0xFFFFE7B3), modifier = Modifier.size(34.dp)) { Icon(Icons.Default.Shield, null, tint = Color(0xFFB36A00), modifier = Modifier.padding(8.dp)) }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("PERSONAL RECOMMENDATION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(state.recommendation.title, style = MaterialTheme.typography.titleMedium)
                    }
                }
                Text(state.recommendation.relevance, color = MausamColors.teal, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
            Text(state.recommendation.summary, style = MaterialTheme.typography.bodyLarge)
            Surface(shape = RoundedCornerShape(14.dp), color = MausamColors.sky) {
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = MausamColors.blue, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("${state.recommendation.whenText} · ${state.recommendation.whereText}", style = MaterialTheme.typography.bodySmall, color = MausamColors.ink)
                }
            }
            Text(state.recommendation.actionText, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.showExplanation(true) }, modifier = Modifier.weight(1f)) { Text("Why this matters") }
                OutlinedButton(onClick = { viewModel.showTrace(true) }) { Icon(Icons.Default.Info, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Trace") }
            }
            Text("Was this useful?", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                listOf("Helpful", "Not useful", "Wrong timing").forEach { feedback ->
                    AssistChip(onClick = { viewModel.submitFeedback(feedback) }, label = { Text(feedback) })
                }
            }
            state.lastFeedback?.let { Text("Feedback saved: $it", color = MausamColors.teal, style = MaterialTheme.typography.labelSmall) }
        }
    }
}

@Composable
private fun RiskCard(state: AppUiState) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FC))) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Risk snapshot", style = MaterialTheme.typography.titleMedium)
                Text("Explainable ranking", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                RiskPill("Rain", state.risks.rain, Color(0xFFE35B5B))
                RiskPill("Heat", state.risks.heat, Color(0xFFE69A2C))
                RiskPill("Wind", state.risks.wind, Color(0xFF4C8DEB))
                RiskPill("Travel", state.risks.travel, Color(0xFF9B68D8))
            }
        }
    }
}

@Composable
private fun RiskPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(42.dp).clip(CircleShape).background(color.copy(alpha = .14f)), contentAlignment = Alignment.Center) { Text(value.take(1), color = color, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ComparisonCard(state: AppUiState) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Home vs college", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PlaceWeather(state.profile.places[0], Modifier.weight(1f))
                PlaceWeather(state.profile.places[1], Modifier.weight(1f))
            }
            Text("Your college location has significantly higher rain risk.", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun PlaceWeather(place: SavedPlace, modifier: Modifier) {
    Surface(modifier, shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.padding(14.dp)) {
            Text(place.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            Text(place.city, style = MaterialTheme.typography.titleMedium)
            Text("${place.temperature}°", style = MaterialTheme.typography.headlineSmall)
            Text("Rain ${place.rainChance}%", color = if (place.rainChance > 60) MausamColors.red else MausamColors.teal, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun PrioritiesCard(state: AppUiState) {
    val priorities = com.mausamiq.app.domain.IntelligenceEngine.prioritizedSections(state.profile)
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Explore, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(8.dp)); Text("Your homepage priority", style = MaterialTheme.typography.titleMedium) }
            Text("The same forecast is reordered for your ${state.profile.primaryPersona().label.lowercase()} context.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            priorities.forEachIndexed { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Surface(shape = CircleShape, color = if (index == 0) MausamColors.blue else Color(0xFFE8EDF6), modifier = Modifier.size(26.dp)) { Text("${index + 1}", modifier = Modifier.wrapContentHeight().padding(top = 4.dp).fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = if (index == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(10.dp)); Text(item, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun AlertsScreen(state: AppUiState, viewModel: AppViewModel) {
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { ScreenHeader("Smart alerts", "Only what matters, when it matters") }
        item {
            Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFEAF7F1)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = MausamColors.teal)
                    Spacer(Modifier.width(10.dp)); Text("Alerts are deduplicated and ranked against your context.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        items(state.alerts) { alert -> AlertCard(alert) }
        item {
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Notification timing", style = MaterialTheme.typography.titleMedium)
                    Text("Smart timing is set to notify before your usual departure, with critical-alert override.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("Notifications enabled"); Switch(checked = state.profile.notificationsEnabled, onCheckedChange = viewModel::toggleNotifications) }
                }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: WeatherAlert) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Notifications, null, tint = MausamColors.red); Spacer(Modifier.width(8.dp)); Text(alert.title, style = MaterialTheme.typography.titleMedium) }
                Text(alert.priority, style = MaterialTheme.typography.labelSmall, color = MausamColors.red, fontWeight = FontWeight.Bold)
            }
            Text(alert.detail, style = MaterialTheme.typography.bodyMedium)
            Text("${alert.location} · ${alert.time}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Divider()
            Text("Consolidated event · Forecast and interpretation are shown separately", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun PlacesScreen(state: AppUiState) {
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { ScreenHeader("Places & travel", "Monitor the locations that matter") }
        item {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MausamColors.blue)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.DirectionsCar, null, tint = Color.White); Spacer(Modifier.width(8.dp)); Text("Smart Travel", color = Color.White, style = MaterialTheme.typography.titleMedium) }
                    Text("Chennai → Coimbatore", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                    Text("Destination weather is available. Route-level weather is unavailable in demo mode.", color = Color.White.copy(alpha = .82f), style = MaterialTheme.typography.bodySmall)
                    Surface(shape = RoundedCornerShape(14.dp), color = Color.White.copy(alpha = .15f)) { Text("Suggested departure window: review before leaving", color = Color.White, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
        item { Text("Saved locations", style = MaterialTheme.typography.titleLarge) }
        items(state.profile.places) { place ->
            Card(shape = RoundedCornerShape(20.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = MausamColors.sky, modifier = Modifier.size(42.dp)) { Icon(if (place.label == "HOME") Icons.Default.Home else Icons.Default.LocationOn, null, tint = MausamColors.blue, modifier = Modifier.padding(10.dp)) }
                    Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(place.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold); Text(place.city, style = MaterialTheme.typography.titleMedium) }
                    Column(horizontalAlignment = Alignment.End) { Text("${place.temperature}°", style = MaterialTheme.typography.titleMedium); Text("Rain ${place.rainChance}%", style = MaterialTheme.typography.labelSmall, color = if (place.rainChance > 60) MausamColors.red else MausamColors.teal) }
                }
            }
        }
        item {
            OutlinedButton(onClick = { }) { Icon(Icons.Default.AddLocation, null); Spacer(Modifier.width(8.dp)); Text("Add saved place") }
        }
    }
}

@Composable
private fun ProfileScreen(state: AppUiState, viewModel: AppViewModel) {
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { ScreenHeader("Personalization center", "You control what MausamIQ learns") }
        item {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MausamColors.blue)) {
                Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = .18f), modifier = Modifier.size(54.dp)) { Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.padding(14.dp)) }
                    Spacer(Modifier.width(14.dp)); Column { Text(state.profile.name, color = Color.White, style = MaterialTheme.typography.titleLarge); Text("${state.profile.feedbackCount} feedback signals · adaptive ranking", color = Color.White.copy(alpha = .76f), style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
        item { Text("Your personas", style = MaterialTheme.typography.titleLarge) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Persona.values().toList().chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { persona ->
                            FilterChip(selected = state.profile.personas.contains(persona), onClick = { viewModel.togglePersona(persona) }, label = { Text(persona.label, maxLines = 1, overflow = TextOverflow.Ellipsis) }, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingRow("Adaptive personalization", "Use feedback to adjust prominence", state.profile.adaptivePersonalization, viewModel::toggleAdaptive)
                    Divider()
                    SettingRow("Smart notifications", "Alert before relevant routines", state.profile.notificationsEnabled, viewModel::toggleNotifications)
                    Divider()
                    SettingRow("Quiet hours", "Pause non-critical alerts", state.profile.quietHours, viewModel::toggleQuietHours)
                    Divider()
                    SettingRow("Offline cache preview", "Show last valid forecast with freshness label", state.isOffline, viewModel::setOffline)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(8.dp)); Text("Privacy and trust", style = MaterialTheme.typography.titleMedium) }
                    Text("Precise location is optional. Health-related weather preferences are optional and consent-based. Demo data is never presented as live.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Last profile context: ${nowLabel()}", style = MaterialTheme.typography.labelSmall)
                    TextButton(onClick = viewModel::resetProfile) { Text("Reset my weather profile", color = MausamColors.red) }
                }
            }
        }
        item { Spacer(Modifier.height(10.dp)) }
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.titleMedium); Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun ScreenHeader(title: String, subtitle: String) {
    Column(Modifier.padding(top = 18.dp, bottom = 6.dp)) { Text(title, style = MaterialTheme.typography.headlineSmall); Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium) }
}

@Composable
private fun ExplanationDialog(state: AppUiState, viewModel: AppViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.showExplanation(false) },
        confirmButton = { TextButton(onClick = { viewModel.showExplanation(false) }) { Text("Done") } },
        title = { Text("Why am I seeing this?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(state.recommendation.summary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                ExplanationLine("WHAT", state.recommendation.summary)
                ExplanationLine("WHEN", state.recommendation.whenText)
                ExplanationLine("WHERE", state.recommendation.whereText)
                ExplanationLine("WHY YOU", state.recommendation.whyText)
                ExplanationLine("ACTION", state.recommendation.actionText)
                Divider()
                state.recommendation.factors.forEach { Text("✓ $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
    )
}

@Composable
private fun ExplanationLine(label: String, text: String) { Column { Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold); Text(text, style = MaterialTheme.typography.bodySmall) } }

@Composable
private fun DecisionTraceDialog(state: AppUiState, viewModel: AppViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.showTrace(false) },
        confirmButton = { TextButton(onClick = { viewModel.showTrace(false) }) { Text("Close") } },
        title = { Text("AI decision trace") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("DEMO MODE · Rule-based personalization", style = MaterialTheme.typography.labelSmall, color = MausamColors.amber, fontWeight = FontWeight.Bold)
                TraceBlock("INPUT", listOf("Rain probability: ${state.collegeWeather.rainChance}%", "Location: ${state.collegeWeather.city}", "Time: 08:00", "Persona: ${state.profile.primaryPersona().label}"))
                TraceBlock("CONTEXT", listOf("Travel period overlaps forecast", "Saved location and routine match"))
                TraceBlock("PRIORITY", listOf(state.risks.travel))
                TraceBlock("OUTPUT", listOf(state.recommendation.title))
            }
        }
    )
}

@Composable
private fun TraceBlock(title: String, lines: List<String>) { Column { Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold); lines.forEach { Text(it, style = MaterialTheme.typography.bodySmall) } } }

