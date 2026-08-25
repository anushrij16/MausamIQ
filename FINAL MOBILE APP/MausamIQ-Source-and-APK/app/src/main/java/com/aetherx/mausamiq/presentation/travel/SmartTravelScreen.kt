package com.aetherx.mausamiq.presentation.travel

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.aetherx.mausamiq.core.designsystem.BrandEmerald
import com.aetherx.mausamiq.core.designsystem.BrandPrimary
import com.aetherx.mausamiq.core.designsystem.BrandPrimaryLight
import com.aetherx.mausamiq.core.designsystem.BrandRose
import com.aetherx.mausamiq.core.designsystem.CardBorderDark
import com.aetherx.mausamiq.core.designsystem.components.GlassCard

@Composable
fun SmartTravelScreen(
    viewModel: SmartTravelViewModel,
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
                        Color(0xFF0F172A),
                        Color(0xFF070B12)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(BrandPrimary.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Explore,
                        contentDescription = null,
                        tint = BrandPrimaryLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    Text(
                        text = "Smart Travel Planner",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Intercity transit & road weather risk analysis",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Route Input Form Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x660F172A),
                borderColor = CardBorderDark
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.origin,
                        onValueChange = { viewModel.onOriginChange(it) },
                        label = { Text("Departure Origin") },
                        leadingIcon = { Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = BrandPrimaryLight) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimaryLight,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = state.destination,
                        onValueChange = { viewModel.onDestinationChange(it) },
                        label = { Text("Travel Destination") },
                        leadingIcon = { Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = BrandAmber) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimaryLight,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = state.departureTime,
                        onValueChange = { viewModel.onTimeChange(it) },
                        label = { Text("Departure Time") },
                        leadingIcon = { Icon(Icons.Rounded.Schedule, contentDescription = null, tint = Color(0xFFA78BFA)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimaryLight,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Button(
                        onClick = { viewModel.analyzeRoute() },
                        enabled = !state.isAnalyzing,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        if (state.isAnalyzing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Rounded.DirectionsCar, contentDescription = null)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Analyze Travel Weather Risks", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            // Analysis Result
            state.travelPlan?.let { plan ->
                val riskColor = when (plan.weatherRisk) {
                    "SEVERE" -> BrandRose
                    "CAUTION" -> BrandAmber
                    else -> BrandEmerald
                }

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0x661E1B4B),
                    borderColor = riskColor.copy(alpha = 0.6f)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ROUTE RISK ADVISORY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = riskColor
                            )
                            Box(
                                modifier = Modifier
                                    .background(riskColor.copy(alpha = 0.2f), CircleShape)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = plan.weatherRisk,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = riskColor
                                )
                            }
                        }

                        Text(
                            text = plan.travelAdvisory,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )

                        // Metric Indicators
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.WaterDrop, contentDescription = null, tint = BrandPrimaryLight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.size(4.dp))
                                Text("Rain: ${plan.destinationRainProbability}%", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Visibility, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.size(4.dp))
                                Text("Vis: ${plan.visibilityKm} km", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Speed, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.size(4.dp))
                                Text("Wind: ${plan.destinationWindSpeed} km/h", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                            }
                        }

                        // Recommended Travel Window
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x3310B981), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Recommended Travel Window", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = BrandEmerald)
                                Text(plan.recommendedTravelWindow, style = MaterialTheme.typography.bodySmall, color = Color(0xFFA7F3D0))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
