package com.mausamiq.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF14213D)
private val Cloud = Color(0xFFF6F8FC)
private val Blue = Color(0xFF2E6CE6)
private val Sky = Color(0xFFEAF2FF)
private val Teal = Color(0xFF0FA3B1)
private val Amber = Color(0xFFFFB547)
private val Red = Color(0xFFD94B4B)

private val LightColors = lightColorScheme(
    primary = Blue,
    onPrimary = Color.White,
    primaryContainer = Sky,
    onPrimaryContainer = Ink,
    secondary = Teal,
    background = Cloud,
    surface = Color.White,
    onBackground = Ink,
    onSurface = Ink,
    error = Red
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9DBBFF),
    primaryContainer = Color(0xFF28497E),
    secondary = Color(0xFF7FD6DE),
    background = Color(0xFF101522),
    surface = Color(0xFF182033),
    onBackground = Color(0xFFEAF0FF),
    onSurface = Color(0xFFEAF0FF),
    error = Color(0xFFFFB4AB)
)

private val MausamTypography = Typography().run {
    copy(
        displaySmall = displaySmall.copy(fontWeight = FontWeight.Bold, fontSize = 32.sp),
        headlineSmall = headlineSmall.copy(fontWeight = FontWeight.Bold),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.Bold),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.SemiBold),
        bodyLarge = bodyLarge.copy(lineHeight = 24.sp),
        labelLarge = labelLarge.copy(fontWeight = FontWeight.SemiBold)
    )
}

@Composable
fun MausamIQTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = MausamTypography,
        content = content
    )
}

object MausamColors {
    val ink = Ink
    val blue = Blue
    val sky = Sky
    val teal = Teal
    val amber = Amber
    val red = Red
}
