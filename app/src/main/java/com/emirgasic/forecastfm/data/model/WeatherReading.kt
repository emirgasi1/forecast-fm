package com.emirgasic.forecastfm.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class WeatherReading(
    val label: String,
    val value: String,
    val icon: ImageVector
)