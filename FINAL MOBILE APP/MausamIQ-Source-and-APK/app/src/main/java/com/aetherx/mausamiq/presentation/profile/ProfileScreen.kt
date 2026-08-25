package com.aetherx.mausamiq.presentation.profile

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
import androidx.compose.material.icons.rounded.DirectionsTransit
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Schedule
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
import com.aetherx.mausamiq.domain.model.UserPersona

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
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

            // Profile Header Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x660F172A),
                borderColor = CardBorderDark,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(BrandPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.size(16.dp))

                    Column {
                        Text(
                            text = state.user?.fullName ?: "Explorer",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = state.user?.email ?: "demo@mausamiq.ai",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0x330284C7), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${state.persona.iconEmoji} ${state.persona.title}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = BrandPrimaryLight
                            )
                        }
                    }
                }
            }

            // Weather Personality Section
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x661E1B4B),
                borderColor = Color(0x55818CF8)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Psychology,
                            contentDescription = null,
                            tint = Color(0xFFA5B4FC),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Your Weather Personality",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Text(
                        text = state.persona.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCBD5E1)
                    )
                    Text(
                        text = "Primary Focus: ${state.persona.primaryConcern}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = BrandAmber
                    )
                }
            }

            // Commute Schedule Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x660F172A),
                borderColor = CardBorderDark
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = BrandPrimaryLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Daily Commute Schedule",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Departure Time", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text(state.user?.departureTime ?: "08:30 AM", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                        Column {
                            Text("Return Time", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text(state.user?.returnTime ?: "05:00 PM", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    }
                }
            }

            // Saved Locations
            Text(
                text = "Saved Key Locations",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            state.savedLocations.forEach { loc ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = null,
                                tint = BrandPrimaryLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Column {
                                Text(loc.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Text(loc.type, style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            }
                        }
                        if (loc.isPrimary) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0x3310B981), CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Primary", style = MaterialTheme.typography.labelSmall, color = Color(0xFF34D399))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
