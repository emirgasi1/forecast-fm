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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.common.ForecastItem
import com.emirgasic.forecastfm.core.ui.components.home.PlaylistCard
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.common.WeatherCard

@Composable
fun HomeScreen(
    mainNavController: NavController,
    rootNavController: NavController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val home by viewModel.home.collectAsState()
    if (home == null) {
        return
    }

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
                        text = home!!.greeting,
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
                    text = home!!.weather.location,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                WeatherCard(
                    temperature = home!!.weather.temperature,
                    weather = home!!.weather.condition,
                    feelsLike = home!!.weather.feelsLike,
                    humidity = home!!.weather.humidity,
                    wind = home!!.weather.wind
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
                            shape = MaterialTheme.shapes.medium
                        )
                        .clickable { rootNavController.navigate(Routes.Weather) }
                ) {

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(26.dp)
                    ) {

                        items(home!!.forecast) { forecast ->

                            ForecastItem(
                                icon = forecast.icon,
                                day = forecast.time,
                                temperature = forecast.temperature
                            )

                        }

                        items(home!!.forecast) { forecast ->

                            ForecastItem(
                                icon = forecast.icon,
                                day = forecast.time,
                                temperature = forecast.temperature
                            )

                        }

                        items(home!!.forecast) { forecast ->

                            ForecastItem(
                                icon = forecast.icon,
                                day = forecast.time,
                                temperature = forecast.temperature
                            )

                        }

                        items(home!!.forecast) { forecast ->

                            ForecastItem(
                                icon = forecast.icon,
                                day = forecast.time,
                                temperature = forecast.temperature
                            )

                        }

                        items(home!!.forecast) { forecast ->

                            ForecastItem(
                                icon = forecast.icon,
                                day = forecast.time,
                                temperature = forecast.temperature
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

                    home!!.playlists.forEach { playlist ->

                        PlaylistCard(
                            title = playlist.title,
                            genre = playlist.genre,
                            artwork = playlist.albumImage,
                            likes = playlist.likes.toString(),
                            onClick = {
                                rootNavController.navigate(Routes.Playlist)
                            }
                        )

                    }

                }
            }
        }
    }
}


