package com.crotsertech.waterairandyoumvp.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================
//  SNEP - Frutiger Aero / Apple Aqua
//  Glossy glass-morphic gel, translucent, vibrant aqua/teal
// ============================================================

// --- SNEP Dark (glossy obsidian, midnight blue, deep glass) ---
val SnepDarkBg = Color(0xFF080818)
val SnepDarkSurface = Color(0xCC1A1A30)
val SnepDarkSurfaceVariant = Color(0xCC242444)
val SnepDarkCardTop = Color(0xCC1E1E38)
val SnepDarkCardBot = Color(0xCC181830)
val SnepDarkBorder = Color(0x60FFFFFF)
val SnepDarkInk = Color(0xFFF2F2F7)
val SnepDarkInk2 = Color(0xFFA0A0B0)
val SnepDarkSlate = Color(0xFF707090)
val SnepDarkWater = Color(0xFF0A84FF)
val SnepDarkWater2 = Color(0xFF0055CC)
val SnepDarkAir = Color(0xFF30D158)
val SnepDarkAir2 = Color(0xFF248A3D)
val SnepDarkWarn = Color(0xFFFF453A)
val SnepDarkGold = Color(0xFFFF9F0A)
val SnepDarkMist = Color(0xFF141428)
val SnepDarkTopbar = Color(0xFF060614)
val SnepDarkGlow = Color(0xFF0A84FF).copy(alpha = 0.12f)
val SnepDarkGlassHighlight = Color.White.copy(alpha = 0.25f)
val SnepDarkEtchShadow = Color.Black.copy(alpha = 0.45f)
val SnepDarkInnerBorder = Color.White.copy(alpha = 0.20f)

// --- SNEP Light (sky blue/teal/green gradient, glossy white glass) ---
val SnepLightBg = Color(0xFFB4E0F0)
val SnepLightSurface = Color(0xE8FFFFFF)
val SnepLightSurfaceVariant = Color(0xD8F0F8FF)
val SnepLightCard = Color(0xE8FFFFFF)
val SnepLightBorder = Color(0x80FFFFFF)
val SnepLightInk = Color(0xFF1C1C1E)
val SnepLightInk2 = Color(0xFF636366)
val SnepLightSlate = Color(0xFF8E8E93)
val SnepLightWater = Color(0xFF007AFF)
val SnepLightWater2 = Color(0xFF0055CC)
val SnepLightAir = Color(0xFF34C759)
val SnepLightAir2 = Color(0xFF248A3D)
val SnepLightWarn = Color(0xFFFF3B30)
val SnepLightGold = Color(0xFFFF9500)
val SnepLightMist = Color(0xFFD4E8F0)
val SnepLightTopbar = Color(0xFF0055CC)
val SnepLightGlow = Color(0xFF007AFF).copy(alpha = 0.10f)
val SnepLightGlassHighlight = Color.White.copy(alpha = 0.55f)
val SnepLightEtchShadow = Color.White.copy(alpha = 0.60f)
val SnepLightInnerBorder = Color.White.copy(alpha = 0.50f)

// ============================================================
//  METRO - Windows 8: flat, no shadows/gradients, Segoe UI
// ============================================================

// --- Metro Dark ---
val MetroDarkBg = Color(0xFF111111)
val MetroDarkSurface = Color(0xFF1F1F1F)
val MetroDarkSurfaceVariant = Color(0xFF2A2A2A)
val MetroDarkCard = Color(0xFF2A2A2A)
val MetroDarkBorder = Color(0xFF333333)
val MetroDarkInk = Color(0xFFFFFFFF)
val MetroDarkInk2 = Color(0xFFA0A0A0)
val MetroDarkSlate = Color(0xFF666666)
val MetroDarkWater = Color(0xFF0078D7)
val MetroDarkWater2 = Color(0xFF005A9E)
val MetroDarkAir = Color(0xFF00B294)
val MetroDarkAir2 = Color(0xFF008A72)
val MetroDarkWarn = Color(0xFFE81123)
val MetroDarkGold = Color(0xFFFFB900)
val MetroDarkMist = Color(0xFF1A1A1A)
val MetroDarkTopbar = Color(0xFF000000)

// --- Metro Light ---
val MetroLightBg = Color(0xFFFFFFFF)
val MetroLightSurface = Color(0xFFFFFFFF)
val MetroLightSurfaceVariant = Color(0xFFF2F2F2)
val MetroLightCard = Color(0xFFF2F2F2)
val MetroLightBorder = Color(0xFFD6D6D6)
val MetroLightInk = Color(0xFF000000)
val MetroLightInk2 = Color(0xFF555555)
val MetroLightSlate = Color(0xFF767676)
val MetroLightWater = Color(0xFF0078D7)
val MetroLightWater2 = Color(0xFF005A9E)
val MetroLightAir = Color(0xFF00A4A6)
val MetroLightAir2 = Color(0xFF008A72)
val MetroLightWarn = Color(0xFFE81123)
val MetroLightGold = Color(0xFFFFB900)
val MetroLightMist = Color(0xFFE5E5E5)
val MetroLightTopbar = Color(0xFF0078D7)

@Immutable
data class WayColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val outline: Color,
    val outlineVariant: Color,
    val error: Color,
    val errorContainer: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val isDark: Boolean,
    val isMetro: Boolean,
    val radius: Float = 14f,
    val radiusLg: Float = 22f,
    val topbarHeight: Float = 56f,
    val cardGradientStart: Color = surface,
    val cardGradientEnd: Color = surface,
    val glowColor: Color = Color.Transparent,
    val glassHighlightColor: Color = Color.Transparent,
    val etchedTextShadowColor: Color = Color.Transparent,
    val innerBorderColor: Color = Color.Transparent
)

data class ThemeState(
    val useMetro: Boolean,
    val useDark: Boolean,
    val onThemeChanged: (useMetro: Boolean, useDark: Boolean) -> Unit = { _, _ -> }
)

private val LocalWayColors = staticCompositionLocalOf<WayColors> {
    error("No WayColors provided")
}

val LocalThemeState = staticCompositionLocalOf { ThemeState(false, false) }

@Composable
fun WayTheme(
    useMetro: Boolean = false,
    useDark: Boolean = isSystemInDarkTheme(),
    onThemeChanged: (useMetro: Boolean, useDark: Boolean) -> Unit = { _, _ -> },
    content: @Composable () -> Unit
) {
    val colors = remember(useMetro, useDark) {
        if (useMetro) {
            if (useDark) {
                WayColors(
                    background = MetroDarkBg,
                    surface = MetroDarkSurface,
                    surfaceVariant = MetroDarkSurfaceVariant,
                    primary = MetroDarkWater,
                    primaryContainer = MetroDarkWater2,
                    secondary = MetroDarkAir,
                    onBackground = MetroDarkInk,
                    onSurface = MetroDarkInk,
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    outline = MetroDarkBorder,
                    outlineVariant = MetroDarkBorder,
                    error = MetroDarkWarn,
                    errorContainer = MetroDarkWarn.copy(alpha = 0.18f),
                    success = Color(0xFF1FA756),
                    warning = MetroDarkGold,
                    info = Color(0xFFB146C2),
                    isDark = true,
                    isMetro = true,
                    radius = 0f,
                    radiusLg = 0f,
                    glassHighlightColor = Color.Transparent,
                    etchedTextShadowColor = Color.Transparent
                )
            } else {
                WayColors(
                    background = MetroLightBg,
                    surface = MetroLightSurface,
                    surfaceVariant = MetroLightSurfaceVariant,
                    primary = MetroLightWater,
                    primaryContainer = MetroLightWater2,
                    secondary = MetroLightAir,
                    onBackground = MetroLightInk,
                    onSurface = MetroLightInk,
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    outline = MetroLightBorder,
                    outlineVariant = MetroLightBorder,
                    error = MetroLightWarn,
                    errorContainer = MetroLightWarn.copy(alpha = 0.18f),
                    success = Color(0xFF1FA756),
                    warning = MetroLightGold,
                    info = Color(0xFF5C2D91),
                    isDark = false,
                    isMetro = true,
                    radius = 0f,
                    radiusLg = 0f,
                    glassHighlightColor = Color.Transparent,
                    etchedTextShadowColor = Color.Transparent
                )
            }
        } else {
            if (useDark) {
                WayColors(
                    background = SnepDarkBg,
                    surface = SnepDarkSurface,
                    surfaceVariant = SnepDarkSurfaceVariant,
                    primary = SnepDarkWater,
                    primaryContainer = SnepDarkWater2,
                    secondary = SnepDarkAir,
                    onBackground = SnepDarkInk,
                    onSurface = SnepDarkInk,
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    outline = SnepDarkBorder,
                    outlineVariant = SnepDarkBorder.copy(alpha = 0.3f),
                    error = SnepDarkWarn,
                    errorContainer = SnepDarkWarn.copy(alpha = 0.18f),
                    success = SnepDarkAir,
                    warning = SnepDarkGold,
                    info = SnepDarkWater,
                    isDark = true,
                    isMetro = false,
                    radius = 16f,
                    radiusLg = 24f,
                    cardGradientStart = SnepDarkCardTop,
                    cardGradientEnd = SnepDarkCardBot,
                    glowColor = SnepDarkGlow,
                    glassHighlightColor = SnepDarkGlassHighlight,
                    etchedTextShadowColor = SnepDarkEtchShadow,
                    innerBorderColor = SnepDarkInnerBorder
                )
            } else {
                WayColors(
                    background = SnepLightBg,
                    surface = SnepLightSurface,
                    surfaceVariant = SnepLightSurfaceVariant,
                    primary = SnepLightWater,
                    primaryContainer = SnepLightWater2,
                    secondary = SnepLightAir,
                    onBackground = SnepLightInk,
                    onSurface = SnepLightInk,
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    outline = SnepLightBorder,
                    outlineVariant = SnepLightBorder.copy(alpha = 0.5f),
                    error = SnepLightWarn,
                    errorContainer = SnepLightWarn.copy(alpha = 0.18f),
                    success = SnepLightAir,
                    warning = SnepLightGold,
                    info = SnepLightWater,
                    isDark = false,
                    isMetro = false,
                    radius = 16f,
                    radiusLg = 24f,
                    cardGradientStart = SnepLightCard,
                    cardGradientEnd = SnepLightSurface,
                    glowColor = SnepLightGlow,
                    glassHighlightColor = SnepLightGlassHighlight,
                    etchedTextShadowColor = SnepLightEtchShadow,
                    innerBorderColor = SnepLightInnerBorder
                )
            }
        }
    }

    val darkOnSurfaceVariant = if (useMetro) Color(0xFFB0B0B0) else Color(0xFFB8B8CC)
    val lightOnSurfaceVariant = if (useMetro) Color(0xFF555555) else Color(0xFF636366)

    val colorScheme = if (colors.isDark) darkColorScheme(
        primary = colors.primary,
        primaryContainer = colors.primaryContainer,
        secondary = colors.secondary,
        background = colors.background,
        surface = colors.surface,
        surfaceVariant = colors.surfaceVariant,
        onBackground = colors.onBackground,
        onSurface = colors.onSurface,
        onSurfaceVariant = darkOnSurfaceVariant,
        onPrimary = colors.onPrimary,
        onSecondary = colors.onSecondary,
        outline = colors.outline,
        outlineVariant = colors.outlineVariant,
        error = colors.error,
        errorContainer = colors.errorContainer,
    ) else lightColorScheme(
        primary = colors.primary,
        primaryContainer = colors.primaryContainer,
        secondary = colors.secondary,
        background = colors.background,
        surface = colors.surface,
        surfaceVariant = colors.surfaceVariant,
        onBackground = colors.onBackground,
        onSurface = colors.onSurface,
        onSurfaceVariant = lightOnSurfaceVariant,
        onPrimary = colors.onPrimary,
        onSecondary = colors.onSecondary,
        outline = colors.outline,
        outlineVariant = colors.outlineVariant,
        error = colors.error,
        errorContainer = colors.errorContainer,
    )

    val shapes = Shapes(
        small = RoundedCornerShape(colors.radius.dp),
        medium = RoundedCornerShape(colors.radius.dp),
        large = RoundedCornerShape(colors.radiusLg.dp),
        extraLarge = RoundedCornerShape(colors.radiusLg.dp)
    )

    val metroFont = FontFamily.SansSerif

    val typography = if (useMetro) {
        Typography(
            bodyLarge = TextStyle(fontFamily = metroFont, fontWeight = FontWeight.Normal, fontSize = 15.sp),
            bodyMedium = TextStyle(fontFamily = metroFont, fontWeight = FontWeight.Normal, fontSize = 13.sp),
            bodySmall = TextStyle(fontFamily = metroFont, fontWeight = FontWeight.Normal, fontSize = 11.sp),
            labelLarge = TextStyle(fontFamily = metroFont, fontWeight = FontWeight.Medium, fontSize = 14.sp),
            labelMedium = TextStyle(fontFamily = metroFont, fontWeight = FontWeight.Medium, fontSize = 12.sp),
            labelSmall = TextStyle(fontFamily = metroFont, fontWeight = FontWeight.Medium, fontSize = 10.sp),
            titleLarge = TextStyle(fontFamily = metroFont, fontWeight = FontWeight.Bold, fontSize = 22.sp),
            titleMedium = TextStyle(fontFamily = metroFont, fontWeight = FontWeight.Medium, fontSize = 16.sp),
            titleSmall = TextStyle(fontFamily = metroFont, fontWeight = FontWeight.Medium, fontSize = 14.sp),
            headlineMedium = TextStyle(fontFamily = metroFont, fontWeight = FontWeight.Bold, fontSize = 28.sp),
            displaySmall = TextStyle(fontFamily = metroFont, fontWeight = FontWeight.Bold, fontSize = 36.sp)
        )
    } else {
        Typography(
            bodyLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 15.sp),
            bodyMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 13.sp),
            bodySmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 11.sp),
            labelLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 14.sp),
            labelMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 12.sp),
            labelSmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 10.sp),
            titleLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 22.sp),
            titleMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 16.sp),
            titleSmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 14.sp),
            headlineMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 28.sp),
            displaySmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 36.sp)
        )
    }

    val themeState = remember(useMetro, useDark) { ThemeState(useMetro, useDark, onThemeChanged) }

    CompositionLocalProvider(
        LocalWayColors provides colors,
        LocalThemeState provides themeState
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = shapes,
            typography = typography,
            content = content
        )
    }
}

object WayTheme {
    val colors: WayColors
        @Composable
        get() = LocalWayColors.current
}
