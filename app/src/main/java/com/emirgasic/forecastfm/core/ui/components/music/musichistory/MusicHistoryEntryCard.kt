package com.emirgasic.forecastfm.core.ui.components.music.musichistory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.ui.components.common.IconText
import com.emirgasic.forecastfm.core.ui.components.common.InfoRow

@Composable
fun MusicHistoryEntryCard(
    playlist: String,
    weatherIcon: Painter,
    weather: String,
    temperature: String,
    location: String,
    time: String,
    modifier: Modifier = Modifier,
    musicIcon: Painter = painterResource(R.drawable.music)
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            IconText(
                icon = musicIcon,
                text = playlist
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = weatherIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                InfoRow(
                    first = weather,
                    second = temperature,
                    third = location
                )

            }

            Text(
                text = time,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium
            )

        }

    }

}