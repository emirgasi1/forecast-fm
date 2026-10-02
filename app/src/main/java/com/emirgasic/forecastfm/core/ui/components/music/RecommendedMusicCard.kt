package com.emirgasic.forecastfm.core.ui.components.music

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun RecommendedMusicCard(
    id: String,
    image: String?,
    title: String,
    genre: String,
    mood: String,
    likes: String,
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit,
    onViewPlaylistClick: (String) -> Unit
) {

    val forecastColors = LocalForecastColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = MaterialTheme.shapes.medium,
                ambientColor = forecastColors.shadow.copy(alpha = 0.25f),
                spotColor = forecastColors.shadow.copy(alpha = 0.4f)
            )
            .clickable { onViewPlaylistClick(id) },
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, forecastColors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = forecastColors.card
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = forecastColors.border,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (image.isNullOrBlank()) {
                    Icon(
                        painter = painterResource(R.drawable.album_disc),
                        contentDescription = "Album placeholder",
                        modifier = Modifier.size(52.dp),
                        tint = forecastColors.muted
                    )
                } else {
                    AsyncImage(
                        model = image,
                        contentDescription = "Album cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = forecastColors.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "$genre · $mood",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = forecastColors.muted,
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.clickable { onPlayClick() },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.music),
                            contentDescription = "Play",
                            modifier = Modifier.size(18.dp),
                            tint = forecastColors.primary
                        )
                        Text(
                            text = "Play",
                            color = forecastColors.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.heart),
                            contentDescription = "Likes",
                            modifier = Modifier.size(16.dp),
                            tint = forecastColors.muted
                        )
                        Text(
                            text = likes,
                            color = forecastColors.muted,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}