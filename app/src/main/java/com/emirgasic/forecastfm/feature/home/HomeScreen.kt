package com.emirgasic.forecastfm.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.common.ForecastItem
import com.emirgasic.forecastfm.core.ui.components.home.PlaylistCard
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.common.WeatherCard

@Composable
fun HomeScreen(mainNavController: NavController,
               rootNavController: NavController,
               modifier: Modifier =Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
    ) {
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
                WeatherCard(
                    temperature = "12°C",
                    weather = "Sunny",
                    feelsLike = "Thunderous",
                    humidity = "Strong",
                    wind = "20 km/h"
                )
                Spacer(modifier.height(26.dp))
                SectionTitle(
                    title = "5-day Forecast"
                )
                Spacer(modifier.height(38.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.55f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            ),
                            shape = MaterialTheme.shapes.medium)
                        .clickable{rootNavController.navigate(Routes.Weather)}
                ) {

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(26.dp)
                    ) {

                        item {
                            ForecastItem(
                                icon = R.drawable.sun,
                                day = "Mon",
                                temperature = "12°C"
                            )
                        }

                        item {
                            ForecastItem(
                                icon = R.drawable.sunny_cloudy,
                                day = "Tue",
                                temperature = "2°C"
                            )
                        }

                        item {
                            ForecastItem(
                                icon = R.drawable.sun,
                                day = "Wed",
                                temperature = "22°C"
                            )
                        }

                        item {
                            ForecastItem(
                                icon = R.drawable.sunny_cloudy,
                                day = "Thu",
                                temperature = "10°C"
                            )
                        }

                        item {
                            ForecastItem(
                                icon = R.drawable.heavy_rain,
                                day = "Fri",
                                temperature = "-2°C"
                            )
                        }
                    }
                }
                Spacer(modifier.height(26.dp))
                SectionTitle(
                    title = "Today's Soundtrack"
                )
                Spacer(modifier = modifier.height(20.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    PlaylistCard(
                        title = "Midnight Coffee",
                        genre = "Lo-fi",
                        artwork = R.drawable.fire,
                        likes = "54",
                        onClick = {
                            rootNavController.navigate(Routes.Playlist)
                        }
                    )


                    PlaylistCard(
                        title = "Downtown Coffee",
                        genre = "Jazz",
                        artwork = R.drawable.like,
                        likes = "10",
                        onClick = {
                            rootNavController.navigate(Routes.Playlist)
                        }
                    )


                    // Playlist 3
                    PlaylistCard(
                        title = "Morning Coffee",
                        genre = "Rock",
                        artwork = R.drawable.okay,
                        likes = "302",
                        onClick = {
                            rootNavController.navigate(Routes.Playlist)
                        }
                    )

                    }
                }
            }
        }
    }


