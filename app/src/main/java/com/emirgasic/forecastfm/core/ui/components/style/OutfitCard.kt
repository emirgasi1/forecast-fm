package com.emirgasic.forecastfm.core.ui.components.style

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.ui.modifiers.pressScale
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun OutfitCard(
    imageUrl: String,
    title: String,
    weatherCondition: String,
    season: String,
    likes: Int = 0,
    isSaved: Boolean = false,
    onLikeClick: () -> Unit = {},
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(forecastColors.card)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(forecastColors.surface),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(R.drawable.placeholder) // Shows placeholder if it fails
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = forecastColors.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$weatherCondition • $season",
                    style = MaterialTheme.typography.bodySmall,
                    color = forecastColors.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = if (isSaved) {
                                painterResource(R.drawable.heart_filled)
                            } else {
                                painterResource(R.drawable.heart)
                            },
                            contentDescription = if (isSaved) "Unsave" else "Save",
                            modifier = Modifier.size(18.dp),
                            tint = if (isSaved) {
                                forecastColors.primary
                            } else {
                                forecastColors.muted
                            }
                        )
                    }
                    Text(
                        text = likes.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = forecastColors.muted
                    )
                }
            }
        }
    }
}