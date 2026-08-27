package com.emirgasic.forecastfm.core.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun PlaylistRecommendationCard(
    title: String,
    genre: String,
    songs: String,
    duration: String,
    likes: String,
    likeIcon: Painter,
    playIcon: Painter,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        )
    ) {

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = genre,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                IconButton(
                    onClick = onFavoriteClick
                ) {

                    Icon(
                        imageVector =
                            if (isFavorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                        contentDescription =
                            if (isFavorite) {
                                "Remove from favorites"
                            } else {
                                "Add to favorites"
                            }
                    )
                }
            }

            InfoRow(
                first = songs,
                second = duration
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconText(
                    icon = likeIcon,
                    text = likes
                )

                IconText(
                    icon = playIcon,
                    text = "Play"
                )

            }

        }

    }

}