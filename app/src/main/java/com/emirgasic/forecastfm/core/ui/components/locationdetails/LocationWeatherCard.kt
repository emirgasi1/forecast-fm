package com.emirgasic.forecastfm.core.ui.components.locationdetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.core.ui.components.common.WeatherReadingsGrid
import com.emirgasic.forecastfm.data.model.WeatherReading
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun LocationWeatherCard(
    weatherIcon: Painter,
    condition: String,
    temperature: String,
    humidity: String,
    wind: String,
    feelsLike: String? = null,
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = 1.dp,
            color = forecastColors.border
        ),
        colors = CardDefaults.cardColors(
            containerColor = forecastColors.card
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = weatherIcon,
                    contentDescription = "Weather",
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = condition,
                    style = MaterialTheme.typography.bodyLarge,
                    color = forecastColors.body
                )
                Text(
                    text = temperature,
                    style = MaterialTheme.typography.bodyLarge,
                    color = forecastColors.body
                )
            }

            WeatherReadingsGrid(
                readings = buildList {
                    add(
                        WeatherReading(
                            label = "Temperature",
                            value = temperature,
                            icon = Icons.Default.Thermostat
                        )
                    )
                    if (feelsLike != null) {
                        add(
                            WeatherReading(
                                label = "Feels like",
                                value = feelsLike,
                                icon = Icons.Default.WbTwilight
                            )
                        )
                    }
                    add(
                        WeatherReading(
                            label = "Humidity",
                            value = humidity,
                            icon = Icons.Default.WaterDrop
                        )
                    )
                    add(
                        WeatherReading(
                            label = "Wind",
                            value = wind,
                            icon = Icons.Default.Air
                        )
                    )
                }
            )
        }
    }
}