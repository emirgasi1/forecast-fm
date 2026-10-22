package com.emirgasic.forecastfm.core.ui.components.place

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun PlaceInfoHero(
    name: String,
    category: String,
    locationName: String?,
    openingHours: String?,
    outdoorSeating: Boolean,
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        forecastColors.primary,
                        forecastColors.primaryDark
                    )
                )
            )
            .padding(24.dp)
    ) {

        Text(
            text = category.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.85f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.background
        )

        if (!locationName.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = locationName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.85f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!openingHours.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Hours",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = openingHours,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }

            if (outdoorSeating) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Outdoor Seating",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Outdoor",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }
        }
    }
}