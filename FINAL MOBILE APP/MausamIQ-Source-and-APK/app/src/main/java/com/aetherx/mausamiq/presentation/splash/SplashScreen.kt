package com.aetherx.mausamiq.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherx.mausamiq.core.designsystem.BrandAmber
import com.aetherx.mausamiq.core.designsystem.BrandPrimary
import com.aetherx.mausamiq.core.designsystem.BrandPrimaryLight
import com.aetherx.mausamiq.core.designsystem.components.WeatherCanvasBackground
import com.aetherx.mausamiq.core.designsystem.components.WeatherVisualType
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        delay(1200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030712),
                        Color(0xFF0B132B),
                        Color(0xFF070B12)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        WeatherCanvasBackground(type = WeatherVisualType.NIGHT_CLEAR)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated Logo Emblem
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(scale.value)
                    .alpha(alpha.value)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                BrandPrimary.copy(alpha = 0.8f),
                                BrandPrimaryLight.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WbSunny,
                        contentDescription = null,
                        tint = BrandAmber,
                        modifier = Modifier.size(44.dp)
                    )
                    Icon(
                        imageVector = Icons.Rounded.Cloud,
                        contentDescription = null,
                        tint = BrandPrimaryLight,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                text = "MAUSAMIQ",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                ),
                color = Color.White,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Not Just Weather. Weather That Matters to You.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = BrandPrimaryLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Team & Hackathon Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.alpha(textAlpha.value)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Bolt,
                    contentDescription = null,
                    tint = BrandAmber,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = " TEAM AETHERX  •  SIH26076",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
