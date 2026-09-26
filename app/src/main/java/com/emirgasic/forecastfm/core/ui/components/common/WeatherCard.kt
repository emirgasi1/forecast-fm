package com.emirgasic.forecastfm.core.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun WeatherCard(
    weather: String,
    temperature: String,
    feelsLike: String,
    humidity: String,
    wind: String,
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.small,
                ambientColor = forecastColors.shadow.copy(alpha = 0.3f),
                spotColor = forecastColors.shadow.copy(alpha = 0.5f)
            ),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = forecastColors.border
        ),
        colors = CardDefaults.cardColors(
            containerColor = forecastColors.card
        )
    ) {

        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 20.dp
            )
        ) {

            Text(
                text = "Weather Forecast",
                color = forecastColors.muted,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = temperature,
                color = forecastColors.title,
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = weather,
                color = forecastColors.body,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            WeatherInfoRow(
                title = "Feels like:",
                value = feelsLike
            )

            WeatherInfoRow(
                title = "Humidity:",
                value = humidity
            )

            WeatherInfoRow(
                title = "Wind:",
                value = wind
            )
        }
    }
}