package com.emirgasic.forecastfm.core.ui.components.map.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LegendTabContent(
    modifier: Modifier = Modifier
) {
    val legendItems = listOf(
        LegendItem("You", Color(0xFF2196F3)),
        LegendItem("Venues", Color(0xFFD97706)),
        LegendItem("Places", Color(0xFFD6A77A)),
        LegendItem("Selected", Color(0xFF6B8E4E)),
        LegendItem("Bus Stops", Color(0xFFF4C96B)),
        LegendItem("Areas", Color(0xFFD97706).copy(alpha = 0.3f))
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Map Legend",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        legendItems.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(item.color)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

data class LegendItem(
    val label: String,
    val color: Color
)