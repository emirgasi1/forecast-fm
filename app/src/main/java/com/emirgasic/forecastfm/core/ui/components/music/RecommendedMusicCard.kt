package com.emirgasic.forecastfm.core.ui.components.music

import android.R.attr.id
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.ui.components.common.IconText
import com.emirgasic.forecastfm.core.ui.components.common.InfoRow

@Composable
fun RecommendedMusicCard(
    id:String,
    image: Painter,
    title: String,
    genre: String,
    mood: String,
    likes: String,
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit,
    onViewPlaylistClick: (String) -> Unit
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            Image(
                painter = image,
                contentDescription = "Album cover",
                modifier = Modifier
                    .size(300.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.medium
                    )
            )


            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge
            )


            InfoRow(
                first = genre,
                second = mood
            )


            Row(
                horizontalArrangement = Arrangement.spacedBy(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconText(
                    icon = painterResource(R.drawable.music),
                    text = "Play",
                    onClick = onPlayClick
                )


                IconText(
                    icon = painterResource(R.drawable.heart),
                    text = likes
                )

            }


            TextButton(
                onClick = {
                    onViewPlaylistClick(id.toString())
                }
            ) {

                Text(
                    text = "View Playlist",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )

            }

        }

    }
}