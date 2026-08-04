package com.emirgasic.forecastfm.core.ui.components.locationdetails.locationrecommendation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.core.ui.components.common.InfoRow


@Composable
fun PlaceRecommendationCard(
    name: String,
    category: String,
    location: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )


            InfoRow(
                first = category,
                second = location
            )


            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )


            TextButton(
                onClick = onClick,
                modifier = Modifier.align(Alignment.End)
            ) {

                Text(
                    text = "View Details",
                    color = MaterialTheme.colorScheme.primary
                )

            }

        }

    }

}