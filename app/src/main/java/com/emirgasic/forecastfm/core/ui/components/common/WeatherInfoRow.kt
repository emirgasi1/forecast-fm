package com.emirgasic.forecastfm.core.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun WeatherInfoRow(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    Row(
        modifier = modifier.padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.Start
    ) {

        Text(
            text = title,
            color = forecastColors.muted,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            text = value,
            color = forecastColors.body,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}