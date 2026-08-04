package com.emirgasic.forecastfm.core.ui.components.locationdetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun PlaceDiscoveryCard(

    location: String,

    weatherIcon: Painter,

    weather: String,

    temperature: String,

    playlist: String,

    outfit: String,

    onChooseClick: () -> Unit,

    modifier: Modifier = Modifier

) {


    Card(

        modifier = modifier.fillMaxWidth(),

        shape = MaterialTheme.shapes.medium,

        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        )

    ) {


        Column(

            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {


            Text(

                text = location,

                style = MaterialTheme.typography.headlineSmall,

                color = MaterialTheme.colorScheme.onSurfaceVariant

            )


            Row(

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement = Arrangement.spacedBy(12.dp)

            ) {


                Icon(

                    painter = weatherIcon,

                    contentDescription = "Weather",

                    modifier = Modifier.size(24.dp)

                )


                Text(

                    text = weather,

                    style = MaterialTheme.typography.titleMedium

                )


                Text(

                    text = temperature,

                    style = MaterialTheme.typography.titleMedium

                )


            }


            Column(

                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {


                Text(

                    text = "🎵 $playlist",

                    style = MaterialTheme.typography.bodyLarge

                )


                Text(

                    text = "👕 $outfit",

                    style = MaterialTheme.typography.bodyLarge

                )

            }



            Button(

                modifier = Modifier.fillMaxWidth(),

                onClick = onChooseClick

            ) {

                Text(
                    text = "Choose on your own"
                )

            }


        }

    }

}