package com.emirgasic.forecastfm.core.ui.components.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.ui.components.common.IconText

@Composable
fun LocationRecommendationCard(
    location: String,
    weatherIcon: Painter,
    temperature: String,
    weather: String,
    music: String,
    outfit: String,
    modifier: Modifier = Modifier,
    onViewDetailsClick: () -> Unit = {}
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            IconText(
                icon = painterResource(R.drawable.mappin),
                text = location
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconText(
                    icon = weatherIcon,
                    text = temperature
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "•",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = weather,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )

            }

            IconText(
                icon = painterResource(R.drawable.music),
                text = music
            )

            IconText(
                icon = painterResource(R.drawable.clothes),
                text = outfit
            )

            Button(
                onClick = onViewDetailsClick
            ) {

                Text("View Details")

            }

        }

    }

}