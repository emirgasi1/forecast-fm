package com.emirgasic.forecastfm.core.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun ProfileStatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = value,
            color = forecastColors.title,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = label,
            color = forecastColors.muted,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}