package com.crotsertech.waterairandyoumvp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.crotsertech.waterairandyoumvp.theme.WayTheme

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val colors = WayTheme.colors
    val shape = RoundedCornerShape(colors.radiusLg.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(colors.cardGradientStart, colors.cardGradientEnd),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val inset = strokeWidth / 2f + 0.5.dp.toPx()
                val path = Path().apply {
                    addRoundRect(
                        androidx.compose.ui.geometry.RoundRect(
                            left = inset, top = inset,
                            right = size.width - inset, bottom = size.height - inset,
                            radiusX = colors.radiusLg.dp.toPx() - 0.5.dp.toPx(),
                            radiusY = colors.radiusLg.dp.toPx() - 0.5.dp.toPx()
                        )
                    )
                }
                drawPath(path, color = colors.innerBorderColor, style = Stroke(width = strokeWidth))
            }
    ) {
        // Liquid glass specular highlight - top 50% white-to-transparent gradient reflection
        if (!colors.isMetro && colors.glassHighlightColor.alpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.TopCenter)
                    .clip(shape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.7f),
                                Color.White.copy(alpha = 0f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp)
                .let { if (onClick != {}) it.clickable(onClick = onClick) else it }
        ) {
            content()
        }
    }
}
