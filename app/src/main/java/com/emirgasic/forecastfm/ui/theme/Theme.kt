package com.emirgasic.forecastfm.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.emirgasic.forecastfm.core.theme.AppTheme

data class ForecastColors(
    val primary: Color,
    val primaryDark: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color,
    val surface: Color,
    val card: Color,
    val border: Color,
    val shadow: Color,
    val title: Color,
    val body: Color,
    val muted: Color,
    val success: Color,
    val warning: Color,
    val error: Color
)

val MorningColors = ForecastColors(
    primary = MorningPrimary,
    primaryDark = MorningPrimaryDark,
    secondary = MorningSecondary,
    accent = MorningAccent,
    background = MorningBackground,
    surface = MorningSurface,
    card = MorningCard,
    border = MorningBorder,
    shadow = MorningShadow,
    title = MorningTitle,
    body = MorningBody,
    muted = MorningMuted,
    success = MorningSuccess,
    warning = MorningWarning,
    error = MorningError
)

val AfternoonColors = ForecastColors(
    primary = AfternoonPrimary,
    primaryDark = AfternoonPrimaryDark,
    secondary = AfternoonSecondary,
    accent = AfternoonAccent,
    background = AfternoonBackground,
    surface = AfternoonSurface,
    card = AfternoonCard,
    border = AfternoonBorder,
    shadow = AfternoonShadow,
    title = AfternoonTitle,
    body = AfternoonBody,
    muted = AfternoonMuted,
    success = AfternoonSuccess,
    warning = AfternoonWarning,
    error = AfternoonError
)

val NightColors = ForecastColors(
    primary = NightPrimary,
    primaryDark = NightPrimaryDark,
    secondary = NightSecondary,
    accent = NightAccent,
    background = NightBackground,
    surface = NightSurface,
    card = NightCard,
    border = NightBorder,
    shadow = NightShadow,
    title = NightTitle,
    body = NightBody,
    muted = NightMuted,
    success = NightSuccess,
    warning = NightWarning,
    error = NightError
)

val LocalForecastColors = staticCompositionLocalOf { MorningColors }

fun forecastColorsFor(theme: AppTheme): ForecastColors {
    return when (theme) {
        AppTheme.MORNING -> MorningColors
        AppTheme.AFTERNOON -> AfternoonColors
        AppTheme.NIGHT -> NightColors
        AppTheme.AUTO -> MorningColors
    }
}

val MorningColorScheme = lightColorScheme(
    primary = MorningPrimary,
    onPrimary = MorningTitle,
    secondary = MorningSecondary,
    onSecondary = MorningTitle,
    tertiary = MorningAccent,
    onTertiary = MorningTitle,
    background = MorningBackground,
    onBackground = MorningBody,
    surface = MorningSurface,
    onSurface = MorningBody,
    surfaceVariant = MorningCard,
    onSurfaceVariant = MorningMuted,
    outline = MorningBorder,
    outlineVariant = MorningBorder,
    error = MorningError,
    onError = MorningSurface,
    inverseSurface = MorningPrimaryDark,
    inverseOnSurface = MorningBackground,
    scrim = MorningBorder
)

val AfternoonColorScheme = lightColorScheme(
    primary = AfternoonPrimary,
    onPrimary = AfternoonTitle,
    secondary = AfternoonSecondary,
    onSecondary = AfternoonTitle,
    tertiary = AfternoonAccent,
    onTertiary = AfternoonTitle,
    background = AfternoonBackground,
    onBackground = AfternoonBody,
    surface = AfternoonSurface,
    onSurface = AfternoonBody,
    surfaceVariant = AfternoonCard,
    onSurfaceVariant = AfternoonMuted,
    outline = AfternoonBorder,
    outlineVariant = AfternoonBorder,
    error = AfternoonError,
    onError = AfternoonSurface,
    inverseSurface = AfternoonPrimaryDark,
    inverseOnSurface = AfternoonBackground,
    scrim = AfternoonBorder
)

val NightColorScheme = darkColorScheme(
    primary = NightPrimary,
    onPrimary = NightTitle,
    secondary = NightSecondary,
    onSecondary = NightTitle,
    tertiary = NightAccent,
    onTertiary = NightTitle,
    background = NightBackground,
    onBackground = NightBody,
    surface = NightSurface,
    onSurface = NightBody,
    surfaceVariant = NightCard,
    onSurfaceVariant = NightMuted,
    outline = NightBorder,
    outlineVariant = NightBorder,
    error = NightError,
    onError = NightSurface,
    inverseSurface = NightPrimaryDark,
    inverseOnSurface = NightBackground,
    scrim = NightBorder
)

fun colorSchemeFor(theme: AppTheme): androidx.compose.material3.ColorScheme {
    return when (theme) {
        AppTheme.MORNING -> MorningColorScheme
        AppTheme.AFTERNOON -> AfternoonColorScheme
        AppTheme.NIGHT -> NightColorScheme
        AppTheme.AUTO -> MorningColorScheme
    }
}

@Composable
fun ForecastfmTheme(
    colorScheme: androidx.compose.material3.ColorScheme = MorningColorScheme,
    forecastColors: ForecastColors = MorningColors,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalForecastColors provides forecastColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}