package com.crotsertech.waterairandyoumvp.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crotsertech.waterairandyoumvp.theme.WayTheme

@Composable
fun WayModal(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String = "",
    subtitle: String = "",
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = WayTheme.colors
    val modalShape = if (colors.isMetro) {
        RoundedCornerShape(0.dp)
    } else {
        RoundedCornerShape(topStart = colors.radiusLg.dp, topEnd = colors.radiusLg.dp)
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                shape = modalShape,
                color = if (colors.isMetro) MaterialTheme.colorScheme.surface else Color.Transparent,
                tonalElevation = if (colors.isMetro) 0.dp else 0.dp
            ) {
                if (!colors.isMetro) {
                    // Glass-morphic modal background with gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(modalShape)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        if (colors.isDark) Color(0xE01A1A34) else Color(0xEEFFFFFF),
                                        if (colors.isDark) Color(0xD0141428) else Color(0xD8E8F4FF)
                                    )
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
                                // Only draw inner border on top corners
                                drawPath(path, color = colors.innerBorderColor, style = Stroke(width = strokeWidth))
                            }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Liquid glass gloss strip on modal top (SNEP only)
                            if (colors.glassHighlightColor.alpha > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clip(modalShape)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.White.copy(alpha = 0.6f),
                                                    Color.White.copy(alpha = 0f)
                                                )
                                            )
                                        )
                                )
                            }

                            if (title.isNotBlank() || subtitle.isNotBlank()) {
                                Column(
                                    modifier = Modifier.padding(20.dp, 16.dp, 20.dp, 8.dp)
                                ) {
                                    if (title.isNotBlank()) {
                                        Text(
                                            text = title,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (subtitle.isNotBlank()) {
                                        Text(
                                            text = subtitle,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                                HorizontalDivider()
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                content()
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (title.isNotBlank() || subtitle.isNotBlank()) {
                            Column(
                                modifier = Modifier.padding(20.dp, 16.dp, 20.dp, 8.dp)
                            ) {
                                if (title.isNotBlank()) {
                                    Text(
                                        text = title,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (subtitle.isNotBlank()) {
                                    Text(
                                        text = subtitle,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                            HorizontalDivider()
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            content()
                        }
                    }
                }
            }
        }
    }
}
