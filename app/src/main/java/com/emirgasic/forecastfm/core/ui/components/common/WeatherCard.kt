package com.emirgasic.forecastfm.core.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun WeatherCard(
    weather: String,
    temperature: String,
    feelsLike: String,
    humidity: String,
    wind: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {


            Column(
                modifier = Modifier.padding(
                    start = 12.dp,
                    top = 12.dp,
                    end = 12.dp,
                    bottom = 20.dp
                )
            ) {


                Text(
                    text = "Weather Forecast",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall
                )


                Spacer(
                    modifier = Modifier.height(6.dp)
                )


                Text(
                    text = temperature,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineLarge
                )


                Text(
                    text = weather,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


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
}


@Composable
private fun WeatherInfoRow(
    title: String,
    value: String
) {

    Row(
        horizontalArrangement = Arrangement.Start
    ) {

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge
        )


        Spacer(
            modifier = Modifier.width(16.dp)
        )


        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )

    }

}