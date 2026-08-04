package com.emirgasic.forecastfm.core.ui.components.locationdetails.locationrecommendation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CompanionSelector(
    companions: List<String>,
    selectedCompanion: String,
    onCompanionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        companions.forEach { companion ->

            FilterChip(
                selected = selectedCompanion == companion,

                onClick = {
                    onCompanionSelected(companion)
                },

                label = {
                    androidx.compose.material3.Text(
                        text = companion
                    )
                },

                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary
                )
            )

        }

    }

}