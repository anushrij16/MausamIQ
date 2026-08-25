package com.aetherx.mausamiq.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.random.Random

enum class WeatherVisualType {
    SUNNY,
    CLOUDY,
    RAINY,
    THUNDERSTORM,
    NIGHT_CLEAR,
    SNOWY
}

@Composable
fun WeatherCanvasBackground(
    type: WeatherVisualType,
    modifier: Modifier = Modifier
) {
    when (type) {
        WeatherVisualType.SUNNY -> SunnyVisualEffect(modifier)
        WeatherVisualType.CLOUDY -> CloudyVisualEffect(modifier)
        WeatherVisualType.RAINY -> RainyVisualEffect(isStorm = false, modifier = modifier)
        WeatherVisualType.THUNDERSTORM -> RainyVisualEffect(isStorm = true, modifier = modifier)
        WeatherVisualType.NIGHT_CLEAR -> NightVisualEffect(modifier)
        WeatherVisualType.SNOWY -> SnowVisualEffect(modifier)
    }
}

@Composable
fun SunnyVisualEffect(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "sun_transition")
    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sun_pulse"
    )
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sun_rotation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width * 0.82f, size.height * 0.18f)
        val baseRadius = size.width * 0.22f

        // Atmospheric Warm Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x55F59E0B),
                    Color(0x22FBBF24),
                    Color(0x00000000)
                ),
                center = center,
                radius = baseRadius * 2.8f * pulse
            ),
            radius = baseRadius * 2.8f * pulse,
            center = center
        )

        // Core Sun Disc
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFBEB),
                    Color(0xFFFBBF24),
                    Color(0xFFF59E0B)
                ),
                center = center,
                radius = baseRadius * pulse
            ),
            radius = baseRadius * 0.65f,
            center = center
        )
    }
}

@Composable
fun CloudyVisualEffect(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "cloud_transition")
    val offset1 by transition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud_drift_1"
    )
    val offset2 by transition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(38000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud_drift_2"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Cloud Layer 1 (Back Layer)
        drawCloud(
            center = Offset(w * offset1, h * 0.15f),
            scale = 1.3f,
            alpha = 0.18f,
            color = Color(0xFF64748B)
        )

        // Cloud Layer 2 (Front Layer)
        drawCloud(
            center = Offset(w * offset2, h * 0.28f),
            scale = 1.0f,
            alpha = 0.25f,
            color = Color(0xFF94A3B8)
        )
    }
}

private fun DrawScope.drawCloud(center: Offset, scale: Float, alpha: Float, color: Color) {
    val r = 50f * scale
    val c = color.copy(alpha = alpha)
    drawCircle(color = c, radius = r * 1.4f, center = center)
    drawCircle(color = c, radius = r * 1.1f, center = center.copy(x = center.x - r * 1.2f, y = center.y + r * 0.3f))
    drawCircle(color = c, radius = r * 1.2f, center = center.copy(x = center.x + r * 1.3f, y = center.y + r * 0.2f))
    drawCircle(color = c, radius = r * 0.9f, center = center.copy(x = center.x - r * 2.2f, y = center.y + r * 0.6f))
    drawCircle(color = c, radius = r * 0.9f, center = center.copy(x = center.x + r * 2.3f, y = center.y + r * 0.6f))
}

@Composable
fun RainyVisualEffect(
    isStorm: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "rain_transition")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain_fall"
    )

    val lightningAlpha by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (isStorm) 0.85f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lightning_flash"
    )

    val drops = remember {
        List(45) {
            RainDrop(
                xRatio = Random.nextFloat(),
                yOffset = Random.nextFloat(),
                speed = 0.8f + Random.nextFloat() * 0.5f,
                length = 25f + Random.nextFloat() * 30f,
                alpha = 0.25f + Random.nextFloat() * 0.55f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Storm flash overlay
        if (isStorm && lightningAlpha > 0.65f) {
            drawRect(
                color = Color(0xFFE2E8F0).copy(alpha = (lightningAlpha - 0.65f) * 1.2f)
            )
        }

        // Draw animated raindrops
        drops.forEach { drop ->
            val curY = ((drop.yOffset + progress * drop.speed) % 1f) * h
            val curX = drop.xRatio * w + (curY * 0.12f) // slight slant
            drawLine(
                color = Color(0xFF38BDF8).copy(alpha = drop.alpha),
                start = Offset(curX, curY),
                end = Offset(curX + 3f, curY + drop.length),
                strokeWidth = 2.5f
            )
        }
    }
}

private data class RainDrop(
    val xRatio: Float,
    val yOffset: Float,
    val speed: Float,
    val length: Float,
    val alpha: Float
)

@Composable
fun NightVisualEffect(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "star_transition")
    val twinkle by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_twinkle"
    )

    val stars = remember {
        List(35) {
            Offset(Random.nextFloat(), Random.nextFloat() * 0.65f) to (0.3f + Random.nextFloat() * 0.7f)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Moon Glow
        val moonCenter = Offset(w * 0.8f, h * 0.16f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x44E0E7FF),
                    Color(0x116366F1),
                    Color(0x00000000)
                ),
                center = moonCenter,
                radius = 160f
            ),
            radius = 160f,
            center = moonCenter
        )

        // Moon Disc
        drawCircle(
            color = Color(0xFFF1F5F9),
            radius = 32f,
            center = moonCenter
        )
        // Moon Crater shadow (crescent effect)
        drawCircle(
            color = Color(0xFF0F172A),
            radius = 28f,
            center = moonCenter.copy(x = moonCenter.x - 12f, y = moonCenter.y - 4f)
        )

        // Twinkling Stars
        stars.forEach { (pos, baseAlpha) ->
            val alpha = (baseAlpha * twinkle).coerceIn(0f, 1f)
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = 2.2f,
                center = Offset(pos.x * w, pos.y * h)
            )
        }
    }
}

@Composable
fun SnowVisualEffect(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "snow_transition")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "snow_fall"
    )

    val flakes = remember {
        List(30) {
            Offset(Random.nextFloat(), Random.nextFloat()) to (3f + Random.nextFloat() * 4f)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        flakes.forEach { (pos, radius) ->
            val curY = ((pos.y + progress) % 1f) * h
            val sway = kotlin.math.sin((curY / h) * 6.28) * 15f
            val curX = (pos.x * w + sway.toFloat()).coerceIn(0f, w)

            drawCircle(
                color = Color.White.copy(alpha = 0.75f),
                radius = radius,
                center = Offset(curX, curY)
            )
        }
    }
}
