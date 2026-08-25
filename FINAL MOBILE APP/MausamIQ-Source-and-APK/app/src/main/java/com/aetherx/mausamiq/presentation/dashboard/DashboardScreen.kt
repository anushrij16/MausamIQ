package com.aetherx.mausamiq.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherx.mausamiq.core.designsystem.BrandAmber
import com.aetherx.mausamiq.core.designsystem.BrandPrimaryLight
import com.aetherx.mausamiq.core.designsystem.components.GlassCard
import com.aetherx.mausamiq.core.designsystem.components.ShimmerSkeleton
import com.aetherx.mausamiq.core.designsystem.components.WeatherCanvasBackground
import com.aetherx.mausamiq.core.designsystem.components.WeatherVisualType
import com.aetherx.mausamiq.core.utils.DateUtils
import com.aetherx.mausamiq.presentation.insights.ExplainabilityBottomSheetContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToInsights: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Determine visual background based on weather code and day/night
    val visualType = when {
        state.weather == null -> WeatherVisualType.SUNNY
        state.weather?.current?.weatherCode in listOf(95, 96, 99) -> WeatherVisualType.THUNDERSTORM
        state.weather?.current?.weatherCode in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82) -> WeatherVisualType.RAINY
        state.weather?.current?.weatherCode in listOf(71, 73, 75, 77) -> WeatherVisualType.SNOWY
        state.weather?.current?.isDay == false -> WeatherVisualType.NIGHT_CLEAR
        state.weather?.current?.weatherCode in listOf(1, 2, 3) -> WeatherVisualType.CLOUDY
        else -> WeatherVisualType.SUNNY
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030712),
                        Color(0xFF0F172A),
                        Color(0xFF070B12)
                    )
                )
            )
    ) {
        WeatherCanvasBackground(type = visualType)

        if (state.isLoading && state.weather == null) {
            // Loading Skeleton
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShimmerSkeleton(height = 36.dp, modifier = Modifier.fillMaxWidth(0.6f))
                ShimmerSkeleton(height = 200.dp)
                ShimmerSkeleton(height = 140.dp)
                ShimmerSkeleton(height = 120.dp)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Greeting
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${DateUtils.getCurrentGreeting()}, ${state.userName}",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Here's what matters to you today.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.loadDashboardData(forceRefresh = true) },
                        modifier = Modifier.background(Color(0x331E293B), CircleShape)
                    ) {
                        if (state.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = BrandPrimaryLight,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Refresh",
                                tint = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }

                // 1. Hero Weather Card
                state.weather?.let { weatherInfo ->
                    HeroWeatherCard(weather = weatherInfo)
                }

                // 2. Important For You (AI Priority)
                state.insight?.let { insight ->
                    ImportantForYouCard(
                        insight = insight,
                        persona = state.persona,
                        onWhyClick = { viewModel.toggleExplainabilityModal(true) }
                    )
                }

                // 3. AI Recommended Action
                state.insight?.let { insight ->
                    RecommendationCard(insight = insight)
                }

                // 4. Commute Intelligence Card
                state.commutePlan?.let { commute ->
                    CommuteIntelligenceCard(commutePlan = commute)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Explainability Bottom Sheet Modal
        if (state.showExplainabilityModal && state.insight != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleExplainabilityModal(false) },
                sheetState = sheetState,
                containerColor = Color(0xFF0F172A),
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .size(width = 40.dp, height = 4.dp)
                            .background(Color(0xFF334155), CircleShape)
                    )
                }
            ) {
                ExplainabilityBottomSheetContent(
                    insight = state.insight!!,
                    onClose = { viewModel.toggleExplainabilityModal(false) }
                )
            }
        }

        // Location Picker Dialog
        if (state.showLocationPicker) {
            AlertDialog(
                onDismissRequest = { viewModel.toggleLocationPicker(false) },
                title = { Text("Select Saved Location", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.savedLocations.forEach { loc ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x331E293B))
                                    .clickable { viewModel.selectLocation(loc) }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(loc.type.iconEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Column {
                                        Text(loc.name, style = MaterialTheme.typography.titleSmall, color = Color.White)
                                        Text(loc.type.label, style = MaterialTheme.typography.labelSmall, color = BrandPrimaryLight)
                                    }
                                }
                                if (loc.name == state.weather?.locationName) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = BrandPrimaryLight)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.toggleLocationPicker(false) }) {
                        Text("Cancel", color = BrandPrimaryLight)
                    }
                },
                containerColor = Color(0xFF0F172A)
            )
        }
    }
}
