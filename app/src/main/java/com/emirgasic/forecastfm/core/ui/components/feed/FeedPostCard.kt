package com.emirgasic.forecastfm.core.ui.components.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun FeedPostCard(
    profileImage: String,
    username: String,
    time: String,
    weatherIcon: Painter,
    weather: String,
    temperature: String,
    location: String,
    postImage: String,
    playlist: String,
    caption: String,
    likes: String,
    comments: String,
    outfitTitle: String? = null,
    isLiked: Boolean = false,
    isSaved: Boolean = false,
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {

    val forecastColors = LocalForecastColors.current
    var showTimestamp by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
                .clip(MaterialTheme.shapes.medium)
        ) {

            AsyncImage(
                model = postImage,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.placeholder)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.45f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(forecastColors.background.copy(alpha = 0.55f))
                        .border(
                            width = 1.dp,
                            color = forecastColors.border.copy(alpha = 0.35f),
                            shape = CircleShape
                        )
                        .padding(3.dp)
                ) {
                    AsyncImage(
                        model = profileImage,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(R.drawable.profile_picture)
                    )
                }

                ActionIcon(
                    painter = if (isLiked) {
                        painterResource(R.drawable.heart_filled)
                    } else {
                        painterResource(R.drawable.heart)
                    },
                    tint = if (isLiked) forecastColors.error else Color.White,
                    count = likes,
                    onClick = onLikeClick
                )

                ActionIcon(
                    painter = painterResource(R.drawable.comment),
                    tint = Color.White,
                    count = comments,
                    onClick = onCommentClick
                )

                ActionIcon(
                    painter = if (isSaved) {
                        painterResource(R.drawable.bookmark_filled)
                    } else {
                        painterResource(R.drawable.bookmark)
                    },
                    tint = if (isSaved) forecastColors.accent else Color.White,
                    count = null,
                    onClick = onSaveClick
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = weatherIcon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = "$weather • $temperature • $location",
                style = MaterialTheme.typography.bodySmall,
                color = forecastColors.muted
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "@$username",
            style = MaterialTheme.typography.titleSmall,
            color = forecastColors.title,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        if (!outfitTitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "👕 $outfitTitle",
                style = MaterialTheme.typography.bodySmall,
                color = forecastColors.muted,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = caption,
            style = MaterialTheme.typography.bodyMedium,
            color = forecastColors.body,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .clickable { showTimestamp = !showTimestamp }
        )

        AnimatedVisibility(
            visible = showTimestamp,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = forecastColors.muted,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        if (playlist != "No playlist") {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "♫ $playlist",
                style = MaterialTheme.typography.bodySmall,
                color = forecastColors.muted,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}