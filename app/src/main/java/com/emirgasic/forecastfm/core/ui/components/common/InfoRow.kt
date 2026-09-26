package com.emirgasic.forecastfm.core.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun InfoRow(
    first: String,
    second: String,
    third: String? = null,
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = first,
            color = forecastColors.body,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "•",
            color = forecastColors.muted.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = second,
            color = forecastColors.body,
            style = MaterialTheme.typography.titleMedium
        )

        if (third != null) {

            Text(
                text = "•",
                color = forecastColors.muted.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = third,
                color = forecastColors.body,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}