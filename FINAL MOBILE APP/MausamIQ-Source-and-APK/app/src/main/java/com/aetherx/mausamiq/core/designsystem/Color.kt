package com.aetherx.mausamiq.core.designsystem

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand Core Colors
val BrandPrimary = Color(0xFF0284C7)
val BrandPrimaryLight = Color(0xFF38BDF8)
val BrandPrimaryDark = Color(0xFF0369A1)

val BrandAccent = Color(0xFF0EA5E9)
val BrandAmber = Color(0xFFF59E0B)
val BrandSunGold = Color(0xFFFBBF24)
val BrandEmerald = Color(0xFF10B981)
val BrandRose = Color(0xFFEF4444)
val BrandViolet = Color(0xFF8B5CF6)

// Dark Theme Surfaces
val BackgroundDark = Color(0xFF070B12)
val SurfaceDark = Color(0xFF0F172A)
val SurfaceElevatedDark = Color(0xFF1E293B)
val SurfaceGlassDark = Color(0x991E293B)
val CardBorderDark = Color(0x3338BDF8)

// Light Theme Surfaces
val BackgroundLight = Color(0xFFF8FAFC)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceElevatedLight = Color(0xFFF1F5F9)
val SurfaceGlassLight = Color(0xCCFFFFFF)
val CardBorderLight = Color(0x330284C7)

// Text Colors
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextMutedDark = Color(0xFF64748B)

val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)
val TextMutedLight = Color(0xFF94A3B8)

// Weather Atmospheric Gradients
val SunnyGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF0284C7), Color(0xFFF59E0B), Color(0xFF0B111E))
)

val RainyGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF070B12))
)

val StormGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF311042), Color(0xFF0F172A), Color(0xFF030712))
)

val StarryNightGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF030712), Color(0xFF0F172A), Color(0xFF1E1B4B))
)

val CloudyGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A))
)

val GlassCardGradient = Brush.linearGradient(
    colors = listOf(
        Color(0x33FFFFFF),
        Color(0x0DFFFFFF)
    )
)
