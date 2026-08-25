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
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherx.mausamiq.core.designsystem.BrandAmber
import com.aetherx.mausamiq.core.designsystem.BrandPrimary
import com.aetherx.mausamiq.core.designsystem.BrandPrimaryLight
import com.aetherx.mausamiq.core.designsystem.CardBorderDark
import com.aetherx.mausamiq.core.designsystem.components.AnimatedWeatherIcon
import com.aetherx.mausamiq.core.designsystem.components.GlassCard
import com.aetherx.mausamiq.domain.model.WeatherInfo

@Composable
fun HeroWeatherCard(
    weather: WeatherInfo,
    modifier: Modifier = Modifier
) {
    val current = weather.current

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color(0x660F172A),
        borderColor = CardBorderDark,
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp)
        ) {
            // Top Row: Weather Condition Tag & Day/Night Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(BrandPrimary.copy(alpha = 0.25f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = current.conditionName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = BrandPrimaryLight
                    )
                }

                if (weather.isOffline) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEF4444).copy(alpha = 0.2f), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "OFFLINE CACHE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF87171)
                            )
                        )
                    }
                } else {
                    Text(
                        text = weather.lastUpdatedText,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Temperature & Weather Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "${current.temperature.toInt()}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 64.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "°C",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = BrandPrimaryLight
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Text(
                        text = "Feels like ${current.feelsLike.toInt()}°C",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFFCBD5E1)
                    )
                }

                // Animated Weather Icon
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    BrandPrimary.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedWeatherIcon(
                        weatherCode = current.weatherCode,
                        isDay = current.isDay,
                        size = 64.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Weather Metrics Grid (Humidity, Wind, Visibility, Pressure, UV Index)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherMetricItem(
                    icon = Icons.Rounded.WaterDrop,
                    label = "Humidity",
                    value = "${current.humidity}%",
                    tint = Color(0xFF38BDF8)
                )
                WeatherMetricItem(
                    icon = Icons.Rounded.Air,
                    label = "Wind",
                    value = "${current.windSpeedKmH.toInt()} km/h",
                    tint = Color(0xFF34D399)
                )
                WeatherMetricItem(
                    icon = Icons.Rounded.Visibility,
                    label = "Visibility",
                    value = "${current.visibilityKm.toInt()} km",
                    tint = Color(0xFFA78BFA)
                )
                WeatherMetricItem(
                    icon = Icons.Rounded.WbSunny,
                    label = "UV Index",
                    value = String.format("%.1f", current.uvIndex),
                    tint = BrandAmber
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sun Cycle (Sunrise & Sunset)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x221E293B), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.WbSunny, contentDescription = null, tint = BrandAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Sunrise: ${current.sunrise}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.WbSunny, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Sunset: ${current.sunset}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                }
            }
        }
    }
}

@Composable
private fun WeatherMetricItem(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
    }
}
