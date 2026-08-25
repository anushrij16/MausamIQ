package com.aetherx.mausamiq.presentation.map

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AtmosphericRadarVisualizer(
    layer: MapLayerType,
    stations: List<WeatherStationMarker>,
    selectedStation: WeatherStationMarker?,
    onStationClick: (WeatherStationMarker) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "radar_anim")
    val sweepAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_sweep"
    )

    val pulse by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radar_pulse"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(stations) {
                detectTapGestures { tapOffset ->
                    val w = size.width
                    val h = size.height
                    val clicked = stations.find { st ->
                        val stX = st.xRatio * w
                        val stY = st.yRatio * h
                        val distSq = (tapOffset.x - stX) * (tapOffset.x - stX) + (tapOffset.y - stY) * (tapOffset.y - stY)
                        distSq <= 40 * 40 // 40px hit target
                    }
                    if (clicked != null) {
                        onStationClick(clicked)
                    }
                }
            }
    ) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)

        // 1. Draw Radar Range Rings
        val maxRadius = minOf(w, h) * 0.45f
        for (i in 1..4) {
            val r = maxRadius * (i / 4f)
            drawCircle(
                color = Color(0x2238BDF8),
                radius = r,
                center = center,
                style = Stroke(width = 1.5f)
            )
        }

        // Radar Crosshairs
        drawLine(
            color = Color(0x1A38BDF8),
            start = Offset(center.x - maxRadius, center.y),
            end = Offset(center.x + maxRadius, center.y),
            strokeWidth = 1f
        )
        drawLine(
            color = Color(0x1A38BDF8),
            start = Offset(center.x, center.y - maxRadius),
            end = Offset(center.x, center.y + maxRadius),
            strokeWidth = 1f
        )

        // 2. Layer-Specific Overlays
        when (layer) {
            MapLayerType.RAIN_RADAR -> {
                // Precipitation Reflectivity Cells (Doppler Echoes)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xCCEF4444),
                            Color(0xAAF59E0B),
                            Color(0x6610B981),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.35f, h * 0.55f),
                        radius = 120f * pulse
                    ),
                    radius = 120f * pulse,
                    center = Offset(w * 0.35f, h * 0.55f)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xAA38BDF8),
                            Color(0x550284C7),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.65f, h * 0.35f),
                        radius = 90f
                    ),
                    radius = 90f,
                    center = Offset(w * 0.65f, h * 0.35f)
                )

                // Radar Sweep Beam
                val rad = Math.toRadians(sweepAngle.toDouble())
                val beamEnd = Offset(
                    (center.x + maxRadius * cos(rad)).toFloat(),
                    (center.y + maxRadius * sin(rad)).toFloat()
                )
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF38BDF8), Color.Transparent),
                        start = center,
                        end = beamEnd
                    ),
                    start = center,
                    end = beamEnd,
                    strokeWidth = 2.5f
                )
            }

            MapLayerType.TEMPERATURE -> {
                // Temperature Heat Map Contours
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x99F59E0B),
                            Color(0x66EF4444),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.5f, h * 0.45f),
                        radius = maxRadius * 0.9f
                    ),
                    radius = maxRadius * 0.9f,
                    center = Offset(w * 0.5f, h * 0.45f)
                )
            }

            MapLayerType.WIND_STREAM -> {
                // Animated Wind Streamlines
                for (k in 0..6) {
                    val lineY = h * (0.2f + k * 0.1f)
                    val offsetPhase = (sweepAngle * 2f + k * 40f) % w
                    drawLine(
                        color = Color(0x8838BDF8),
                        start = Offset(offsetPhase - 80f, lineY),
                        end = Offset(offsetPhase + 40f, lineY + 10f),
                        strokeWidth = 2.5f,
                        cap = StrokeCap.Round
                    )
                }
            }

            MapLayerType.SEVERE_ALERTS -> {
                // Severe Storm Alert Zones
                drawCircle(
                    color = Color(0x33EF4444),
                    radius = 80f * pulse,
                    center = Offset(w * 0.28f, h * 0.58f)
                )
                drawCircle(
                    color = Color(0xFFEF4444),
                    radius = 12f,
                    center = Offset(w * 0.28f, h * 0.58f)
                )
            }
        }

        // 3. Draw Station Markers
        stations.forEach { st ->
            val pos = Offset(st.xRatio * w, st.yRatio * h)
            val isSelected = selectedStation?.id == st.id

            // Glow around selected station
            if (isSelected) {
                drawCircle(
                    color = Color(0x6638BDF8),
                    radius = 24f,
                    center = pos
                )
            }

            // Outer Ring
            drawCircle(
                color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF0F172A),
                radius = 12f,
                center = pos
            )
            // Core Dot
            drawCircle(
                color = if (isSelected) Color.White else Color(0xFF38BDF8),
                radius = 6f,
                center = pos
            )
        }
    }
}
