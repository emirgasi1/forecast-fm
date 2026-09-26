package com.emirgasic.forecastfm.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.core.ui.components.onboarding.OnboardingOption

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingMusicStep(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        "Pop" to Icons.Default.MusicNote,
        "Indie / Alternative" to Icons.Default.Album,
        "Rock" to Icons.Default.GraphicEq,
        "Hip-Hop" to Icons.Default.Mic,
        "R&B / Soul" to Icons.Default.Audiotrack,
        "Sevdah / Traditional" to Icons.Default.LibraryMusic,
        "Electronic" to Icons.Default.Headphones,
        "Jazz / Blues" to Icons.Default.Piano,
        "Classical" to Icons.Default.Radio
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "What do you listen to?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "We'll tune your playlists",
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