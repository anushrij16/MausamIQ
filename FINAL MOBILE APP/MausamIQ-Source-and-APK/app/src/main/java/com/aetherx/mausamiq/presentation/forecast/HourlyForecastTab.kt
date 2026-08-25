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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.WaterDrop
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
import com.aetherx.mausamiq.core.designsystem.CardBorderDark
import com.aetherx.mausamiq.core.designsystem.components.AnimatedWeatherIcon
import com.aetherx.mausamiq.core.designsystem.components.GlassCard
import com.aetherx.mausamiq.domain.model.HourlyWeather

@Composable
fun HourlyForecastTab(
    hourlyList: List<HourlyWeather>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Temperature Trend Chart Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0x660F172A),
            borderColor = CardBorderDark
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                Text(
                    text = "24-Hour Temperature Curve",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                TemperatureTrendChart(hourlyList = hourlyList)
            }
        }

        // Horizontal Hourly Strip Cards
        Text(
            text = "Hourly Breakdown",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(hourlyList) { hour ->
                HourlyItemCard(hour = hour)
            }
        }

        // Precipitation Probability Bar Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0x660F172A),
            borderColor = CardBorderDark
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                Text(
                    text = "Hourly Precipitation Probability (%)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                PrecipitationBarChart(hourlyList = hourlyList)
            }
        }
    }
}

@Composable
private fun HourlyItemCard(hour: HourlyWeather) {
    GlassCard(
        modifier = Modifier.width(90.dp),
        backgroundColor = Color(0x4D1E293B),
        borderColor = if (hour.precipitationProbability >= 50) Color(0xFF38BDF8) else CardBorderDark,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                text = hour.formattedHour,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedWeatherIcon(
                weatherCode = hour.weatherCode,
                isDay = !hour.formattedHour.contains("12 AM") && !hour.formattedHour.contains("1 AM") && !hour.formattedHour.contains("2 AM") && !hour.formattedHour.contains("3 AM") && !hour.formattedHour.contains("4 AM") && !hour.formattedHour.contains("9 PM") && !hour.formattedHour.contains("10 PM") && !hour.formattedHour.contains("11 PM"),
                size = 32.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${hour.temperature.toInt()}°C",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Rain Probability Chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0x330284C7), CircleShape)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WaterDrop,
                    contentDescription = null,
                    tint = BrandPrimaryLight,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.size(2.dp))
                Text(
                    text = "${hour.precipitationProbability}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = BrandPrimaryLight
                )
            }
        }
    }
}
