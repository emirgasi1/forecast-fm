package com.emirgasic.forecastfm.feature.settings.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutAppScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 60.dp,
                start = 20.dp,
                end = 20.dp,
                bottom = 20.dp
            )
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "About App",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Forecast FM",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Version 1.0",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Paragraph(
            "Forecast FM is a real-time social discovery platform for Sarajevo. It combines live weather, curated music, local places, and outfit suggestions into one experience."
        )

        Paragraph(
            "Discover what's happening around you, share your moments, and find the right soundtrack for every moment of the day."
        )

        Paragraph(
            "Built with Kotlin, Jetpack Compose, and Ktor."
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Made in Sarajevo",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun Paragraph(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}