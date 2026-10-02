package com.emirgasic.forecastfm.core.ui.components.feed.comment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun CommentCard(
    profileImage: String,
    username: String,
    time: String,
    comment: String,
    likes: String,
    isLiked: Boolean = false,
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit = {}
) {

    val forecastColors = LocalForecastColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = MaterialTheme.shapes.medium,
                ambientColor = forecastColors.shadow.copy(alpha = 0.2f),
                spotColor = forecastColors.shadow.copy(alpha = 0.3f)
            ),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, forecastColors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = forecastColors.card
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AsyncImage(
                    model = profileImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.profile_picture)
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = username,
                    style = MaterialTheme.typography.titleMedium,
                    color = forecastColors.title
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = time,
                    style = MaterialTheme.typography.bodySmall,
                    color = forecastColors.muted
                )
            }

            Text(
                text = comment,
                style = MaterialTheme.typography.bodyLarge,
                color = forecastColors.body
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onLikeClick()
                }
            ) {

                Icon(
                    painter = if (isLiked) {
                        painterResource(R.drawable.heart_filled)
                    } else {
                        painterResource(R.drawable.heart)
                    },
                    contentDescription = if (isLiked) "Unlike" else "Like",
                    modifier = Modifier.size(18.dp),
                    tint = if (isLiked) {
                        forecastColors.error
                    } else {
                        forecastColors.muted
                    }
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = likes,
                    style = MaterialTheme.typography.bodySmall,
                    color = forecastColors.muted
                )
            }
        }
    }
}