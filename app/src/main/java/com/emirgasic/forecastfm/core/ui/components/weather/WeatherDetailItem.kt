package com.emirgasic.forecastfm.core.ui.components.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun WeatherDetailItem(
    title: String,
    value: String
) {

    val forecastColors = LocalForecastColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            color = forecastColors.muted,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = value,
            color = forecastColors.title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}