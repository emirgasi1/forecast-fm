package com.emirgasic.forecastfm.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.core.ui.components.onboarding.OnboardingOption

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingPlacesStep(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        "Cafes" to Icons.Default.Coffee,
        "Restaurants" to Icons.Default.Restaurant,
        "Parks & nature" to Icons.Default.Park,
        "Viewpoints" to Icons.Default.Landscape,
        "Historic & culture" to Icons.Default.Museum,
        "Shopping" to Icons.Default.ShoppingBag,
        "Nightlife" to Icons.Default.Nightlife
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "What kind of places?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "We'll surface spots you'll love",
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