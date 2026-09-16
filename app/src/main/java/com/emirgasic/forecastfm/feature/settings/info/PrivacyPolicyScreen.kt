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
fun PrivacyPolicyScreen(
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
            text = "Privacy Policy",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Last updated: 16 September 2026",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Paragraph(
            "Forecast FM is a social discovery app for Sarajevo. We collect only the information required to make the app work: your email, username, and any content you choose to share, such as posts, comments, and profile images."
        )

        Paragraph(
            "Your location is used to display nearby places, weather, and recommendations. Location data is processed on your device and is not stored on our servers."
        )

        Paragraph(
            "Images you upload are stored securely on our servers and displayed only within the app. You can delete any content you create at any time."
        )

        Paragraph(
            "We do not sell your data to third parties. We do not use third-party advertising trackers."
        )

        Paragraph(
            "The app uses the following third-party services: OpenWeatherMap for weather data and YouTube for music playlists. Their own privacy policies apply to the data they process."
        )

        Paragraph(
            "If you would like your account and data deleted, contact us at support@forecastfm.demo."
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