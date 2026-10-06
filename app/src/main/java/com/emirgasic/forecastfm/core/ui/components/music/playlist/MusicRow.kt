package com.emirgasic.forecastfm.core.ui.components.music.playlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.emirgasic.forecastfm.core.ui.modifiers.pressScale
import com.emirgasic.forecastfm.core.utils.formatDuration
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun MusicRow(
    image: String?,
    title: String,
    artist: String,
    duration: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    val forecastColors = LocalForecastColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .pressScale {
                onClick()
            }
            .shadow(
                elevation = 3.dp,
                shape = MaterialTheme.shapes.medium,
                ambientColor = forecastColors.shadow.copy(alpha = 0.25f),
                spotColor = forecastColors.shadow.copy(alpha = 0.4f)
            ),
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
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(forecastColors.surface),
                contentAlignment = Alignment.Center
            ) {
                if (image.isNullOrBlank()) {
                    Icon(
                        painter = painterResource(R.drawable.album_disc),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = forecastColors.muted
                    )
                } else {
                    AsyncImage(
                        model = image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = forecastColors.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = forecastColors.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.bodyMedium,
                color = forecastColors.muted
            )
        }
    }
}
