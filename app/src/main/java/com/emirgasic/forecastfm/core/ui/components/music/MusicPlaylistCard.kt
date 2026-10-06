package com.emirgasic.forecastfm.core.ui.components.music

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.ui.components.common.IconText
import com.emirgasic.forecastfm.core.ui.components.common.InfoRow
import com.emirgasic.forecastfm.core.ui.modifiers.pressScale
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun MusicPlaylistCard(
    title: String,
    genre: String,
    mood: String,
    weather: String,
    temperature: String,
    location: String,
    likes: String,
    isFavorite: Boolean = false,
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {

    val forecastColors = LocalForecastColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .pressScale {
                onClick()
            },
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
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

            Text(
                text = "Mood: $mood",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )

            if (weather.isNotBlank() && location.isNotBlank()) {
                InfoRow(
                    first = "$weather $temperature".trim(),
                    second = location
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {


                Spacer(
                    modifier = Modifier.weight(1f)
                )

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
                            },
                        tint =
                            if (isFavorite) {
                                forecastColors.error
                            } else {
                                forecastColors.muted
                            }
                    )
                }

                IconText(
                    icon = painterResource(R.drawable.play),
                    text = "Open",
                    onClick = onPlayClick
                )
            }
        }
    }
}