package com.emirgasic.forecastfm.core.ui.components.place

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun PlaceInfoAbout(
    description: String,
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    Column(modifier = modifier) {

        Text(
            text = "About",
            style = MaterialTheme.typography.titleLarge,
            color = forecastColors.title
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = forecastColors.body
        )
    }
}