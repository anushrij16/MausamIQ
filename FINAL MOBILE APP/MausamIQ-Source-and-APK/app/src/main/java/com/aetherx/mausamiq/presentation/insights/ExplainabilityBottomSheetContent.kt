package com.aetherx.mausamiq.presentation.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherx.mausamiq.core.designsystem.BrandAmber
import com.aetherx.mausamiq.core.designsystem.BrandEmerald
import com.aetherx.mausamiq.core.designsystem.BrandPrimary
import com.aetherx.mausamiq.core.designsystem.BrandPrimaryLight
import com.aetherx.mausamiq.core.designsystem.BrandRose
import com.aetherx.mausamiq.core.designsystem.components.GlassCard
import com.aetherx.mausamiq.domain.model.ExplainableInsight

@Composable
fun ExplainabilityBottomSheetContent(
    insight: ExplainableInsight,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Modal Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF6366F1).copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Psychology,
                        contentDescription = null,
                        tint = Color(0xFFA5B4FC),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    Text(
                        text = "Why Am I Seeing This?",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Explainable AI Factor Breakdown",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandPrimaryLight
                    )
                }
            }

            IconButton(onClick = onClose) {
                Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
            }
        }

        // Active Recommendation Highlight
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0x4D1E293B)
        ) {
            Column {
                Text(
                    text = "GENERATED RECOMMENDATION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = BrandAmber
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = insight.recommendation,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }

        // Engine Confidence Score Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0x330284C7)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Engine Confidence Score",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                    Text(
                        text = "${insight.confidencePercentage}%",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = BrandEmerald
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { insight.confidencePercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = BrandEmerald,
                    trackColor = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = insight.formulaExplanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1)
                )
            }
        }

        // Calculated Factors List
        Text(
            text = "Contributing Meteorological & Persona Factors",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )

        insight.factors.forEach { factor ->
            val impactColor = when (factor.importance) {
                "CRITICAL", "HIGH" -> BrandRose
                "MEDIUM" -> BrandAmber
                else -> Color(0xFF38BDF8)
            }

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x331E293B)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(factor.iconEmoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.size(10.dp))
                        Column {
                            Text(
                                text = factor.label,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = factor.value,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(impactColor.copy(alpha = 0.2f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${factor.importance} IMPACT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = impactColor
                        )
                    }
                }
            }
        }

        // Meteorological Integrity Note (Probability clarification)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x221E293B), RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                tint = BrandPrimaryLight,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Meteorological Note: A 70% rain probability means there is a 70% statistical likelihood of precipitation in your designated area during that time window, not that 70% of the entire landscape will get wet.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8)
            )
        }

        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
            Text("GOT IT", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
