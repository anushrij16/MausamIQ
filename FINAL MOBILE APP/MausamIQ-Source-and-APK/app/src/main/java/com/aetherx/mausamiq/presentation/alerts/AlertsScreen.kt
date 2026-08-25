package com.aetherx.mausamiq.presentation.alerts

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.aetherx.mausamiq.core.designsystem.BrandRose
import com.aetherx.mausamiq.core.designsystem.CardBorderDark
import com.aetherx.mausamiq.core.designsystem.components.GlassCard
import com.aetherx.mausamiq.domain.model.AlertSeverity
import com.aetherx.mausamiq.domain.model.WeatherAlert

@Composable
fun AlertsScreen(
    viewModel: AlertsViewModel,
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(BrandRose.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsActive,
                        contentDescription = null,
                        tint = BrandRose,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    Text(
                        text = "Smart Weather Alerts",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Impact-driven severe weather notifications",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Severity Filter Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("ALL", "CRITICAL", "HIGH", "MODERATE").forEach { filter ->
                    val isSelected = state.selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.filterBySeverity(filter) },
                        label = { Text(filter, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (filter == "CRITICAL" || filter == "HIGH") BrandRose else BrandPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        )
                    )
                }
            }

            // Alerts Feed
            if (state.filteredAlerts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active severe alerts for your saved locations.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.filteredAlerts, key = { it.id }) { alert ->
                        AlertCardItem(
                            alert = alert,
                            onMarkAsRead = { viewModel.markAsRead(alert.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AlertCardItem(
    alert: WeatherAlert,
    onMarkAsRead: () -> Unit
) {
    val severityColor = when (alert.severity) {
        AlertSeverity.CRITICAL, AlertSeverity.HIGH -> BrandRose
        AlertSeverity.MODERATE -> BrandAmber
        AlertSeverity.INFO -> Color(0xFF38BDF8)
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (alert.isRead) Color(0x331E293B) else Color(0x660F172A),
        borderColor = if (alert.isRead) CardBorderDark else severityColor.copy(alpha = 0.6f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(severityColor.copy(alpha = 0.2f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = alert.severity.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = severityColor,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Text(
                    text = alert.timeWindow,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8)
                )
            }

            Text(
                text = alert.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Text(
                text = alert.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFCBD5E1)
            )

            // Why It Matters Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x331E293B), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Why this matters: ${alert.whyItMatters}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = BrandPrimaryLight
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Action: ${alert.recommendedAction}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = BrandAmber,
                    modifier = Modifier.weight(1f)
                )

                if (!alert.isRead) {
                    Button(
                        onClick = onMarkAsRead,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x3338BDF8))
                    ) {
                        Text("Mark Read", style = MaterialTheme.typography.labelSmall, color = BrandPrimaryLight)
                    }
                }
            }
        }
    }
}
