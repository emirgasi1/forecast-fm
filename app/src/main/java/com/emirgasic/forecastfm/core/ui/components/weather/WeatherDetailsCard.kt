package com.emirgasic.forecastfm.core.ui.components.weather

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
private fun WeatherDetailItem(
    title: String,
    value: String
){

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium
        )


        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
@Composable
fun WeatherDetailsCard(
    feelsLike: String,
    humidity: String,
    wind: String,
    uvIndex: String,
    airQuality: String,
    modifier: Modifier = Modifier
){


    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    ){

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){

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