package com.emirgasic.forecastfm.core.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.R

@Composable
fun PlaylistCard(
    title: String,
    genre: String,
    artwork: Int,
    likes: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge
            )


            Spacer(
                modifier = Modifier.width(8.dp)
            )


            VerticalDivider(
                modifier = Modifier.height(20.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onBackground
            )


            Spacer(
                modifier = Modifier.width(8.dp)
            )


            Text(
                text = genre,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
        }



        Spacer(
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(R.drawable.heart),
                contentDescription = "Likes",
                modifier = Modifier.size(20.dp)
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            if (likes != null) {
                Text(
                    text = likes,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge
                )
            }


        }
    }
}