package com.emirgasic.forecastfm.core.ui.components.editprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun ProfilePhotoEditor(
    image: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    val forecastColors = LocalForecastColors.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Box(
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape)
                .background(forecastColors.card)
                .border(2.dp, forecastColors.border, CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {

            AsyncImage(
                model = image,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(124.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = "Change Photo",
            color = forecastColors.body,
            style = MaterialTheme.typography.titleMedium
        )
    }
}