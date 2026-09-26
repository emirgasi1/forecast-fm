package com.emirgasic.forecastfm.core.ui.components.weather

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun WeatherDetailsCard(
    feelsLike: String,
    humidity: String,
    wind: String,
    uvIndex: String,
    airQuality: String,
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = MaterialTheme.shapes.medium,
                ambientColor = forecastColors.shadow.copy(alpha = 0.25f),
                spotColor = forecastColors.shadow.copy(alpha = 0.4f)
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = forecastColors.card
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, forecastColors.border)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(forecastColors.card)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            WeatherDetailItem(
                title = "Feels Like",
                value = feelsLike
            )

            WeatherDetailItem(
                title = "Humidity",
                value = humidity
            )

            WeatherDetailItem(
                title = "Wind",
                value = wind
            )

            WeatherDetailItem(
                title = "UV Index",
                value = uvIndex
            )

            WeatherDetailItem(
                title = "Air Quality",
                value = airQuality
            )
        }
    }
}