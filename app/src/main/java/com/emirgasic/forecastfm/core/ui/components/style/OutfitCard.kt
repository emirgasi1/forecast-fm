package com.emirgasic.forecastfm.core.ui.components.style

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.core.ui.components.common.InfoRow

@Composable
fun OutfitCard(

    image: Painter,

    title: String,

    weatherCondition: String,

    season: String,

    modifier: Modifier = Modifier,

    onClick: () -> Unit = {}

) {

    Card(

        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable(onClick = onClick),

        shape = MaterialTheme.shapes.large,

        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        ),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )

    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Image(

                painter = image,

                contentDescription = title,

                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)

            )

            Text(

                text = title,

                style = MaterialTheme.typography.titleMedium,

                color = MaterialTheme.colorScheme.onSurfaceVariant

            )

            InfoRow(

                first = weatherCondition,

                second = season

            )

        }

    }

}