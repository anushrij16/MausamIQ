package com.aetherx.mausamiq.presentation.forecast

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
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherx.mausamiq.core.designsystem.BrandAmber
import com.aetherx.mausamiq.core.designsystem.BrandPrimaryLight
import com.aetherx.mausamiq.core.designsystem.CardBorderDark
import com.aetherx.mausamiq.core.designsystem.components.AnimatedWeatherIcon
import com.aetherx.mausamiq.core.designsystem.components.GlassCard
import com.aetherx.mausamiq.domain.model.DailyWeather

@Composable
fun DailyForecastTab(
    dailyList: List<DailyWeather>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        dailyList.forEach { day ->
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x660F172A),
                borderColor = CardBorderDark,
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Day of Week & Date
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(
                            text = day.dayOfWeek,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = day.isoDate.takeLast(5),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // Weather Icon & Rain Probability
                    Row(
                        modifier = Modifier.weight(1.5f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AnimatedWeatherIcon(
                            weatherCode = day.weatherCode,
                            isDay = true,
                            size = 32.dp
                        )
                        if (day.precipitationProbabilityMax > 20) {
                            Spacer(modifier = Modifier.size(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.WaterDrop,
                                    contentDescription = null,
                                    tint = BrandPrimaryLight,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "${day.precipitationProbabilityMax}%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = BrandPrimaryLight
                                )
                            }
                        }
                    }

                    // Temperature Range Bar & Values
                    Row(
                        modifier = Modifier.weight(1.8f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "${day.tempMin.toInt()}°",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        // Visual Temp Span Bar
                        Box(
                            modifier = Modifier
                                .size(width = 60.dp, height = 5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF334155))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(5.dp)
                                    .background(BrandAmber, RoundedCornerShape(3.dp))
                            )
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "${day.tempMax.toInt()}°",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
