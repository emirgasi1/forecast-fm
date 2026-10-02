package com.emirgasic.forecastfm.core.ui.components.music.playlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

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
) {

    val forecastColors = LocalForecastColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.medium,
                ambientColor = forecastColors.shadow.copy(alpha = 0.3f),
                spotColor = forecastColors.shadow.copy(alpha = 0.45f)
            ),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, forecastColors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = forecastColors.card
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(forecastColors.card)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = forecastColors.border,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (album.isNullOrBlank()) {
                    Icon(
                        painter = painterResource(R.drawable.album_disc),
                        contentDescription = "Album placeholder",
                        modifier = Modifier.size(96.dp),
                        tint = forecastColors.muted
                    )
                } else {
                    AsyncImage(
                        model = album,
                        contentDescription = "Album cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Text(
                text = title,
                color = forecastColors.title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = genre,
                    color = forecastColors.body,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "•",
                    color = forecastColors.muted
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = mood,
                    color = forecastColors.body,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = weatherIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = weather,
                    color = forecastColors.body,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = temperature,
                    color = forecastColors.body,
                    style = MaterialTheme.typography.bodyMedium
                )

                Image(
                    painter = locationIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = location,
                    color = forecastColors.body,
                    style = MaterialTheme.typography.bodyMedium
                )

                IconButton(
                    onClick = onFavoriteClick
                ) {
                    Icon(
                        painter = painterResource(
                            if (isFavorite) R.drawable.heart_filled else R.drawable.heart
                        ),
                        contentDescription = if (isFavorite) {
                            "Remove from favorites"
                        } else {
                            "Add to favorites"
                        },
                        tint = if (isFavorite) forecastColors.primary else forecastColors.muted,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}