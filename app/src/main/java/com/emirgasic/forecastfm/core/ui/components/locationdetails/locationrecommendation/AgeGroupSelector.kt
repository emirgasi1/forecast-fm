package com.emirgasic.forecastfm.core.ui.components.locationdetails.locationrecommendation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AgeGroupSelector(
    ageGroups: List<String>,
    selectedAgeGroup: String,
    onAgeGroupSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ageGroups.forEach { ageGroup ->
            FilterChip(
                selected = selectedAgeGroup == ageGroup,
                onClick = {
                    onAgeGroupSelected(ageGroup)
                },
                label = {
                    Text(text = ageGroup)
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}