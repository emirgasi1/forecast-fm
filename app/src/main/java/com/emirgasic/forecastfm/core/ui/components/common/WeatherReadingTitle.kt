package com.emirgasic.forecastfm.core.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.data.model.WeatherReading
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun WeatherReadingTile(
    reading: WeatherReading,
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    Row(
        modifier = modifier
            .heightIn(min = 82.dp)
            .background(
                color = forecastColors.card,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                border = BorderStroke(1.dp, forecastColors.border),
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = reading.label,
                color = forecastColors.muted,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reading.value,
                color = forecastColors.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = reading.icon,
            contentDescription = reading.label,
            tint = forecastColors.primary,
            modifier = Modifier.size(26.dp)
        )
    }
}