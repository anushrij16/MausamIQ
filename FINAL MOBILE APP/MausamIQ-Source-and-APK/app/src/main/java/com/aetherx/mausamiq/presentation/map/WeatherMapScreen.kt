package com.aetherx.mausamiq.presentation.map

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherx.mausamiq.core.designsystem.BrandAmber
import com.aetherx.mausamiq.core.designsystem.BrandPrimary
import com.aetherx.mausamiq.core.designsystem.BrandPrimaryLight
import com.aetherx.mausamiq.core.designsystem.CardBorderDark
import com.aetherx.mausamiq.core.designsystem.components.GlassCard

@Composable
fun WeatherMapScreen(
    viewModel: WeatherMapViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030712),
                        Color(0xFF0B132B),
                        Color(0xFF070B12)
                    )
                )
            )
    ) {
        // Radar Visualization Canvas
        AtmosphericRadarVisualizer(
            layer = state.selectedLayer,
            stations = state.stations,
            selectedStation = state.selectedStation,
            onStationClick = { viewModel.selectStation(it) }
        )

        // Overlay Controls
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header & Layer Switcher
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Layers,
                        contentDescription = null,
                        tint = BrandPrimaryLight,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Atmospheric Radar & Layers",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MapLayerType.entries.forEach { layer ->
                        val isSelected = state.selectedLayer == layer
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectLayer(layer) },
                            label = { Text("${layer.iconEmoji} ${layer.label}", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0x991E293B),
                                labelColor = Color(0xFFCBD5E1)
                            )
                        )
                    }
                }
            }

            // Bottom Selected Station Info Card
            state.selectedStation?.let { station ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xCC0F172A),
                    borderColor = CardBorderDark,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.LocationOn,
                                    contentDescription = null,
                                    tint = BrandPrimaryLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = station.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rain Prob: ${station.rainProb}% • Wind: ${station.windKmH} km/h",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Text(
                            text = "${station.temp.toInt()}°C",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = BrandAmber
                            )
                        )
                    }
                }
            }
        }
    }
}
