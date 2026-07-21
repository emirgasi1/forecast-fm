package com.emirgasic.forecastfm.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R

@Composable
fun HomeScreen(navController: NavController,modifier:Modifier= Modifier){
    Box(modifier=modifier.fillMaxSize().background(color=MaterialTheme.colorScheme.background)) {
        LazyColumn(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = modifier.fillMaxSize().padding(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Good Morning",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Image(
                        painter = painterResource(R.drawable.sun),
                        contentDescription = "Sunny",
                        modifier = modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Bascarsija",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Weather Forecast",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "12°C",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = "Sunny",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                "Feels like:",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier.width(16.dp))
                            Text(
                                "Thunderus",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                "Humidity:",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier.width(16.dp))
                            Text(
                                "Strong",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                "Wind:",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier.width(16.dp))
                            Text(
                                "20km/h",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                Spacer(modifier.height(26.dp))
                Text(
                    text = "5-day Forecast",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier.height(38.dp))
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(26.dp),
                    modifier = modifier.background(color = MaterialTheme.colorScheme.surfaceVariant)
                        .padding(10.dp).clip(MaterialTheme.shapes.medium)
                ) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.sun),
                                contentDescription = "Sun",
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = "Mon",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "12°C",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.sunny_cloudy),
                                contentDescription = "sunny_cloudy",
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = "Tue",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "2°C",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.sun),
                                contentDescription = "Sun",
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = "Wed",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "22°C",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.sunny_cloudy),
                                contentDescription = "sunny_cloudy",
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = "Thu",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "10°C",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.heavy_rain),
                                contentDescription = "cloudy",
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = "Fri",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "-2°C",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
                Spacer(modifier.height(26.dp))
                Text(
                    text = "Today's Soundtrack",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = modifier.height(20.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    // Playlist 1
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Midnight Coffee",
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
                                text = "Lo-fi",
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
                                painter = painterResource(R.drawable.fire),
                                contentDescription = "Fire",
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            Text(
                                text = "54",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }


                    // Playlist 2
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Downtown Coffee",
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
                                text = "Jazz",
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
                                painter = painterResource(R.drawable.like),
                                contentDescription = "Like",
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            Text(
                                text = "10",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }


                    // Playlist 3
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Morning Coffee",
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
                                text = "Rock",
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
                                painter = painterResource(R.drawable.okay),
                                contentDescription = "Okay",
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            Text(
                                text = "302",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }

}