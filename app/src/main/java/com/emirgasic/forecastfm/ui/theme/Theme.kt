package com.emirgasic.forecastfm.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MorningColorScheme = lightColorScheme(
    primary = MorningPrimary,
    secondary = MorningSecondary,
    tertiary = MorningAccent,

    background = MorningBackground,
    surface = MorningSurface,

    onPrimary = MorningTitle,
    onSecondary = MorningTitle,
    onTertiary = MorningTitle,

    onBackground = MorningBody,
    onSurface = MorningBody,

    error = MorningError
)

private val AfternoonColorScheme = lightColorScheme(
    primary = AfternoonPrimary,
    secondary = AfternoonSecondary,
    tertiary = AfternoonAccent,

    background = AfternoonBackground,
    surface = AfternoonSurface,

    onPrimary = AfternoonTitle,
    onSecondary = AfternoonTitle,
    onTertiary = AfternoonTitle,

    onBackground = AfternoonBody,
    onSurface = AfternoonBody,

    error = AfternoonError
)

private val NightColorScheme = darkColorScheme(
    primary = NightPrimary,
    secondary = NightSecondary,
    tertiary = NightAccent,

    background = NightBackground,
    surface = NightSurface,

    onPrimary = NightTitle,
    onSecondary = NightTitle,
    onTertiary = NightTitle,

    onBackground = NightBody,
    onSurface = NightBody,

    error = NightError
)

@Composable
fun ForecastfmTheme(
    colorScheme: androidx.compose.material3.ColorScheme = MorningColorScheme,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}