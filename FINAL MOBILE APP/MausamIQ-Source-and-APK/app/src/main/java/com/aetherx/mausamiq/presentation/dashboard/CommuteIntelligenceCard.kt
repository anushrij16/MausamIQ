package com.aetherx.mausamiq.presentation.dashboard

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsTransit
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
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
import com.aetherx.mausamiq.core.designsystem.BrandPrimaryLight
import com.aetherx.mausamiq.core.designsystem.BrandRose
import com.aetherx.mausamiq.core.designsystem.CardBorderDark
import com.aetherx.mausamiq.core.designsystem.components.GlassCard
import com.aetherx.mausamiq.domain.model.CommutePlan

@Composable
fun CommuteIntelligenceCard(
    commutePlan: CommutePlan,
    modifier: Modifier = Modifier
) {
    val riskColor = when (commutePlan.riskLevel) {
        "HIGH" -> BrandRose
        "MODERATE" -> BrandAmber
        else -> Color(0xFF10B981)
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color(0x660F172A),
        borderColor = CardBorderDark,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(BrandPrimaryLight.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DirectionsTransit,
                            contentDescription = null,
                            tint = BrandPrimaryLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "COMMUTE INTELLIGENCE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = BrandPrimaryLight
                    )
                }

                Box(
                    modifier = Modifier
                        .background(riskColor.copy(alpha = 0.2f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${commutePlan.riskLevel} RISK",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = riskColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Route visual: Origin -> Destination
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FROM",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = commutePlan.originName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.Navigation,
                    contentDescription = null,
                    tint = BrandPrimaryLight,
                    modifier = Modifier.size(18.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "TO",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = commutePlan.destinationName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Advice Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x331E293B), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = if (commutePlan.riskLevel == "HIGH") Icons.Rounded.Warning else Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = riskColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Column {
                    Text(
                        text = "Commute Window Forecast (${commutePlan.estimatedArrival})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFCBD5E1)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = commutePlan.advice,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }
    }
}
