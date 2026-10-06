package com.emirgasic.forecastfm.core.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.ui.modifiers.pressScale
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun PlaylistCard(
    title: String,
    genre: String,
    artwork: String?,
    likes: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    val forecastColors = LocalForecastColors.current

    Row(
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
            )
            .clip(MaterialTheme.shapes.medium)
            .background(forecastColors.card)

            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            color = forecastColors.title,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 120.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        VerticalDivider(
            modifier = Modifier.height(22.dp),
            thickness = 1.dp,
            color = forecastColors.border
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = genre,
            color = forecastColors.muted,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Icon(
            painter = painterResource(R.drawable.heart),
            contentDescription = "Likes",
            modifier = Modifier.size(20.dp),
            tint = forecastColors.muted
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = likes,
            color = forecastColors.body,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}