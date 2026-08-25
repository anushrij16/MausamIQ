package com.aetherx.mausamiq.presentation.forecast

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.aetherx.mausamiq.core.designsystem.BrandAmber
import com.aetherx.mausamiq.core.designsystem.BrandPrimaryLight
import com.aetherx.mausamiq.domain.model.HourlyWeather

@Composable
fun TemperatureTrendChart(
    hourlyList: List<HourlyWeather>,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(hourlyList) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(1000))
    }

    val chartWidth = (hourlyList.size * 64).dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Canvas(
            modifier = Modifier
                .width(chartWidth)
                .height(180.dp)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            if (hourlyList.isEmpty()) return@Canvas

            val minTemp = (hourlyList.minOfOrNull { it.temperature } ?: 15.0).toFloat() - 2f
            val maxTemp = (hourlyList.maxOfOrNull { it.temperature } ?: 35.0).toFloat() + 2f
            val tempRange = (maxTemp - minTemp).coerceAtLeast(1f)

            val spacing = size.width / (hourlyList.size - 1).coerceAtLeast(1)
            val points = mutableListOf<Offset>()

            hourlyList.forEachIndexed { index, item ->
                val x = index * spacing
                val normY = (item.temperature.toFloat() - minTemp) / tempRange
                val y = size.height - (normY * size.height * animProgress.value)
                points.add(Offset(x, y))
            }

            // Draw Gradient Area under Curve
            val fillPath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, size.height)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(points.last().x, size.height)
                    close()
                }
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BrandPrimaryLight.copy(alpha = 0.35f),
                        Color.Transparent
                    )
                )
            )

            // Draw Trend Line
            val strokePath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val p0 = points[i - 1]
                        val p1 = points[i]
                        val cx = (p0.x + p1.x) / 2
                        cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                    }
                }
            }

            drawPath(
                path = strokePath,
                color = BrandPrimaryLight,
                style = Stroke(width = 3.5f, cap = StrokeCap.Round)
            )

            // Draw Data Point Nodes
            points.forEach { pt ->
                drawCircle(
                    color = Color(0xFF0F172A),
                    radius = 6f,
                    center = pt
                )
                drawCircle(
                    color = BrandAmber,
                    radius = 4f,
                    center = pt
                )
            }
        }
    }
}

@Composable
fun PrecipitationBarChart(
    hourlyList: List<HourlyWeather>,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(hourlyList) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(900))
    }

    val chartWidth = (hourlyList.size * 64).dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Canvas(
            modifier = Modifier
                .width(chartWidth)
                .height(140.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            val spacing = size.width / hourlyList.size.coerceAtLeast(1)
            val barWidth = 24f

            hourlyList.forEachIndexed { index, item ->
                val x = index * spacing + (spacing - barWidth) / 2
                val barHeight = (item.precipitationProbability / 100f) * size.height * animProgress.value
                val y = size.height - barHeight

                val barColor = when {
                    item.precipitationProbability >= 70 -> Color(0xFFEF4444)
                    item.precipitationProbability >= 40 -> Color(0xFF38BDF8)
                    else -> Color(0xFF1E293B)
                }

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
            }
        }
    }
}
