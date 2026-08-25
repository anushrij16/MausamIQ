package com.aetherx.mausamiq.presentation.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.aetherx.mausamiq.core.designsystem.components.ShimmerSkeleton
import com.aetherx.mausamiq.domain.model.UserPersona

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AiInsightsScreen(
    viewModel: InsightsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(BrandPrimary.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Psychology,
                        contentDescription = null,
                        tint = BrandPrimaryLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    Text(
                        text = "AI Weather Intelligence",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Real-time persona evaluation engine",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Persona Filter Chips (Live Simulation Switcher)
            Text(
                text = "Simulate Persona Lens:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFCBD5E1)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                UserPersona.entries.forEach { p ->
                    val isSelected = state.selectedPersonaFilter == p
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.switchPersonaFilter(p) },
                        label = { Text("${p.iconEmoji} ${p.title}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        )
                    )
                }
            }

            if (state.isLoading && state.insight == null) {
                ShimmerSkeleton(height = 160.dp)
                ShimmerSkeleton(height = 140.dp)
                ShimmerSkeleton(height = 140.dp)
            } else {
                state.insight?.let { insight ->
                    // 1. Primary AI Recommendation
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color(0x661E1B4B),
                        borderColor = Color(0x55818CF8)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TARGETED ADVISORY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp
                                    ),
                                    color = Color(0xFFA5B4FC)
                                )
                                Box(
                                    modifier = Modifier
                                        .background(BrandEmerald.copy(alpha = 0.2f), CircleShape)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${insight.confidencePercentage}% Confidence",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = BrandEmerald
                                    )
                                }
                            }

                            Text(
                                text = insight.headline,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            Text(
                                text = insight.reason,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFCBD5E1)
                            )

                            Button(
                                onClick = { viewModel.toggleExplainabilitySheet(true) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA))
                            ) {
                                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.size(8.dp))
                                Text("Inspect Decision Factors & Formula")
                            }
                        }
                    }

                    // 2. Weather Risks Breakdown
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color(0x660F172A),
                        borderColor = CardBorderDark
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Key Risk Matrix",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            insight.factors.forEach { factor ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0x331E293B), RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(factor.iconEmoji, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.size(8.dp))
                                        Text(factor.label, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                    }
                                    Text(
                                        factor.value,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = BrandPrimaryLight
                                    )
                                }
                            }
                        }
                    }

                    // 3. Suitability Summary
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color(0x660F172A),
                        borderColor = CardBorderDark
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Persona Context",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = insight.personaContext,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = state.selectedPersonaFilter.primaryConcern,
                                style = MaterialTheme.typography.bodySmall,
                                color = BrandAmber
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Explainability Bottom Sheet
        if (state.showExplainabilitySheet && state.insight != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleExplainabilitySheet(false) },
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
                    onClose = { viewModel.toggleExplainabilitySheet(false) }
                )
            }
        }
    }
}
