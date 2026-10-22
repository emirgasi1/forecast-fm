package com.emirgasic.forecastfm.core.ui.components.map.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
        LegendItem("Your Location", Color(0xFF2196F3)),           // Blue

        LegendItem("Food & Drink", Color(0xFFE05A47)),            // Red
        LegendItem("Cafes & Bakeries", Color(0xFFC47A44)),        // Brown
        LegendItem("Nightlife & Bars", Color(0xFF8E5BB7)),        // Purple

        LegendItem("Hotels & Stay", Color(0xFFB86B77)),           // Pink
        LegendItem("Shopping", Color(0xFF4F86C6)),                // Light Blue
        LegendItem("Parks & Nature", Color(0xFF5E9C62)),          // Green

        LegendItem("Health & Medical", Color(0xFFD32F2F)),        // Strong Red
        LegendItem("Education", Color(0xFF009688)),               // Teal
        LegendItem("Culture & Museums", Color(0xFFD39B3D)),       // Gold

        LegendItem("Neighborhoods", Color(0xFFFF9800)),           // Orange
        LegendItem("Bus Stops & Transit", Color(0xFF7B61FF)),     // Bright Purple
        LegendItem("Other / Services", Color(0xFF9E9E9E))         // Grey
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(item.color)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodyMedium,
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