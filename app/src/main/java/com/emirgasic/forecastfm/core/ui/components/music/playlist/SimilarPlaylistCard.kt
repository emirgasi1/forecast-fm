package com.emirgasic.forecastfm.core.ui.components.music.playlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp


@Composable
fun SimilarPlaylistCard(
    album: Painter,
    title: String,
    genre: String,
    mood: String,
    weatherIcon: Painter,
    weather: String,
    temperature: String,
    locationIcon: Painter,
    location: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
){

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ){

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){

            Image(
                painter = album,
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .clip(MaterialTheme.shapes.medium)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "$genre • $mood",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ){

                Image(
                    painter = weatherIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text("$weather $temperature")

                Spacer(Modifier.width(12.dp))

                Image(
                    painter = locationIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(location)
            }
        }
    }
}