package com.aetherx.mausamiq.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedWeatherIcon(
    weatherCode: Int,
    isDay: Boolean = true,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "weather_icon_anim")
    val pulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_pulse"
    )
    val rotate by transition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_rotate"
    )

    val (icon, tint) = getWeatherIconAndColor(weatherCode, isDay)

    Icon(
        imageVector = icon,
        contentDescription = "Weather Icon",
        tint = tint,
        modifier = modifier
            .size(size)
            .scale(pulse)
            .rotate(if (weatherCode == 0 && isDay) rotate else 0f)
    )
}

fun getWeatherIconAndColor(code: Int, isDay: Boolean): Pair<ImageVector, Color> {
    return when (code) {
        0 -> if (isDay) Pair(Icons.Rounded.WbSunny, Color(0xFFFBBF24)) else Pair(Icons.Rounded.NightsStay, Color(0xFFE2E8F0))
        1, 2 -> Pair(Icons.Rounded.CloudQueue, Color(0xFF38BDF8))
        3 -> Pair(Icons.Rounded.Cloud, Color(0xFF94A3B8))
        45, 48 -> Pair(Icons.Rounded.Air, Color(0xFF64748B))
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> Pair(Icons.Rounded.WaterDrop, Color(0xFF0284C7))
        71, 73, 75, 77, 85, 86 -> Pair(Icons.Rounded.Grain, Color(0xFFE0F2FE))
        95, 96, 99 -> Pair(Icons.Rounded.Thunderstorm, Color(0xFFA855F7))
        else -> Pair(Icons.Rounded.WbSunny, Color(0xFFFBBF24))
    }
}

fun getWeatherConditionName(code: Int): String {
    return when (code) {
        0 -> "Clear Sky"
        1 -> "Mainly Clear"
        2 -> "Partly Cloudy"
        3 -> "Overcast"
        45, 48 -> "Foggy Atmosphere"
        51, 53, 55 -> "Drizzle"
        61, 63 -> "Moderate Rain"
        65 -> "Heavy Rain"
        71, 73, 75 -> "Snowfall"
        80, 81, 82 -> "Rain Showers"
        95 -> "Thunderstorm"
        96, 99 -> "Severe Thunderstorm & Hail"
        else -> "Moderate Weather"
    }
}
