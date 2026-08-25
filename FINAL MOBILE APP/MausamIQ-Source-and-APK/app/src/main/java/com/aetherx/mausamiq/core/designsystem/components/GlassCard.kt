package com.aetherx.mausamiq.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aetherx.mausamiq.core.designsystem.CardBorderDark

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
    borderColor: Color = CardBorderDark,
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Surface(
        modifier = clickableModifier
            .clip(shape),
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x1AFFFFFF),
                            Color(0x00FFFFFF)
                        )
                    )
                )
                .padding(16.dp),
            content = content
        )
    }
}
