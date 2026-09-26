package com.emirgasic.forecastfm.core.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.data.model.WeatherReading

@Composable
fun WeatherReadingsGrid(
    readings: List<WeatherReading>,
    modifier: Modifier = Modifier,
    columns: Int = 2
) {

    val rows = readings.chunked(columns)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { rowReadings ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowReadings.forEach { reading ->
                    WeatherReadingTile(
                        reading = reading,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(columns - rowReadings.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}