package com.emirgasic.forecastfm.core.ui.components.style

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.ui.modifiers.pressScale
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun HeroOutfitCard(
    imageUrl: String,
    title: String,
    weatherCondition: String,
    season: String,
    likes: Int,
    storeName: String?,
    storeAddress: String?,
    price: String?,
    isSaved: Boolean,
    onLikeClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forecastColors = LocalForecastColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight() // 👈 Prevents the card from stretching to the bottom of the screen
            .pressScale { onClick() }
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = forecastColors.shadow.copy(alpha = 0.3f),
                spotColor = forecastColors.shadow.copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, forecastColors.border),
        colors = CardDefaults.cardColors(containerColor = forecastColors.card)
    ) {
        Column {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp) // 👈 Reduced from 350.dp to make it more compact
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(forecastColors.surface)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.placeholder)
                )

                // Weather Pill Overlay
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                    shape = RoundedCornerShape(50),
                    color = forecastColors.background.copy(alpha = 0.85f)
                ) {
                    Text(
                        text = "$weatherCondition • $season",
                        style = MaterialTheme.typography.labelMedium,
                        color = forecastColors.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Details Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = forecastColors.title,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onLikeClick) {
                            Icon(
                                painter = if (isSaved) painterResource(R.drawable.heart_filled) else painterResource(R.drawable.heart),
                                contentDescription = "Save",
                                tint = if (isSaved) forecastColors.primary else forecastColors.muted
                            )
                        }
                        Text(
                            text = likes.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = forecastColors.muted
                        )
                    }
                }

                // Store Info (If it exists)
                if (!storeName.isNullOrBlank()) {
                    Divider(color = forecastColors.border.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = storeName,
                                style = MaterialTheme.typography.titleSmall,
                                color = forecastColors.title
                            )
                            if (!storeAddress.isNullOrBlank()) {
                                Text(
                                    text = storeAddress,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = forecastColors.muted
                                )
                            }
                        }
                        if (!price.isNullOrBlank()) {
                            Text(
                                text = price,
                                style = MaterialTheme.typography.titleMedium,
                                color = forecastColors.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}