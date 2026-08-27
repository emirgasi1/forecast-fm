package com.emirgasic.forecastfm.core.ui.components.music.playlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage


@Composable
fun PlaylistHeaderCard(
    album: String?,
    title: String,
    genre: String,
    mood: String,
    weatherIcon: Painter,
    weather: String,
    temperature: String,
    locationIcon: Painter,
    location: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
){

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        )
    ){

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            AsyncImage(
                model = album,
                contentDescription = "Album cover",
                modifier = Modifier
                    .size(300.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        MaterialTheme.shapes.medium
                    )
            )


            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )


            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){

                Text(
                    text = genre,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "•",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = mood,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }



            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){

                Image(
                    painter = weatherIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(4.dp))

                Text(weather)

                Spacer(Modifier.width(18.dp))

                Text(temperature)

                Spacer(Modifier.width(18.dp))

                Image(
                    painter = locationIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(4.dp))

                Text(location)

                Spacer(Modifier.width(18.dp))

                IconButton(
                    onClick = onFavoriteClick
                ) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Outlined.FavoriteBorder
                        },
                        contentDescription = if (isFavorite) {
                            "Remove from favorites"
                        } else {
                            "Add to favorites"
                        },
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

        }
    }
}