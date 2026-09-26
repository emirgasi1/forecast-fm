package com.emirgasic.forecastfm.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.core.ui.components.onboarding.OnboardingOption

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingWeatherStep(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        "Sunny & clear" to Icons.Default.WbSunny,
        "Partly cloudy" to Icons.Default.Cloud,
        "Rainy" to Icons.Default.Grain,
        "Snowy" to Icons.Default.AcUnit,
        "Cool & crisp" to Icons.Default.WbTwilight,
        "Hot days" to Icons.Default.Whatshot,
        "Mild weather" to Icons.Default.Thermostat
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "What weather do you enjoy?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "We'll suggest places that fit the day",
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