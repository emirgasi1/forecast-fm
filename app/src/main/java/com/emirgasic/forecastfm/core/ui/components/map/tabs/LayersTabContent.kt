package com.emirgasic.forecastfm.core.ui.components.map.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LayersTabContent(
    enabledLayers: Set<String>,
    onLayerToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val layerDescriptions = linkedMapOf(
        "Neighborhoods" to "Suburbs, quarters & general areas",
        "Food & Drink" to "Cafes, restaurants & bakeries",
        "Bars & Nightlife" to "Bars, pubs, clubs & casinos",
        "Shopping" to "Shops, markets & services",
        "Accommodation" to "Hotels, hostels & apartments",
        "Health" to "Hospitals, clinics & pharmacies",
        "Finance & Post" to "Banks, ATMs & post offices",
        "Culture & Education" to "Museums, schools & libraries",
        "Parks & Nature" to "Parks, gardens & viewpoints",
        "Worship" to "Places of worship & monasteries",
        "Transit" to "Bus stops, parking & fuel",
        "Emergency" to "Police & fire stations",
        "Others" to "Uncategorized places"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Map Layers",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        layerDescriptions.forEach { (layer, description) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = layer,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = layer in enabledLayers,
                    onCheckedChange = { onLayerToggle(layer) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.background,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}