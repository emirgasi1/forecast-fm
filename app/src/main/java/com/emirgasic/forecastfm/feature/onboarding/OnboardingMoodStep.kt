package com.emirgasic.forecastfm.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.core.ui.components.onboarding.OnboardingOption

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingMoodStep(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        "Chill & cozy" to Icons.Default.Spa,
        "Energetic & social" to Icons.Default.Celebration,
        "Romantic" to Icons.Default.Favorite,
        "Focused & productive" to Icons.Default.Psychology,
        "Adventurous" to Icons.Default.Explore,
        "Feels good / sunny" to Icons.Default.WbSunny
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "What's your vibe?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "One last thing before we begin",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            maxItemsInEachRow = 2
        ) {
            options.forEach { (label, icon) ->
                OnboardingOption(
                    label = label,
                    selected = label in selected,
                    onClick = { onToggle(label) },
                    icon = icon,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}