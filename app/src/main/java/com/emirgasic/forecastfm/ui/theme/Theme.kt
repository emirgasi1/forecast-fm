package com.emirgasic.forecastfm.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MorningColorScheme = lightColorScheme(
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

private val AfternoonColorScheme = lightColorScheme(
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

private val NightColorScheme = darkColorScheme(
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

@Composable
fun ForecastfmTheme(
    colorScheme: androidx.compose.material3.ColorScheme = MorningColorScheme,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}