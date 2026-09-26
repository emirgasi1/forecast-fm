package com.emirgasic.forecastfm.core.ui.components.place

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun PlaceInfoActions(
    name: String,
    latitude: Double,
    longitude: Double,
    onGetDirectionsClick: () -> Unit,
    onShowOnMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    Column(modifier = modifier) {

        Button(
            onClick = { onGetDirectionsClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = forecastColors.primary,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Text(
                text = "Get Directions",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onShowOnMapClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, forecastColors.border)
        ) {
            Text(
                text = "Show on Map",
                style = MaterialTheme.typography.titleMedium,
                color = forecastColors.title
            )
        }
    }
}