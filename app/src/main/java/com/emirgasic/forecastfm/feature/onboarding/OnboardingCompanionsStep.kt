package com.emirgasic.forecastfm.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.core.ui.components.onboarding.OnboardingOption

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingCompanionsStep(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        "Alone" to Icons.Default.Person,
        "With a partner" to Icons.Default.Favorite,
        "With friends" to Icons.Default.Groups,
        "With family" to Icons.Default.Home,
        "With kids" to Icons.Default.ChildCare
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Who do you go out with?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Pick all that apply",
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