package com.emirgasic.forecastfm.feature.weather
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.weather.CurrentWeatherCard
import com.emirgasic.forecastfm.core.ui.components.weather.ForecastRowItem
import com.emirgasic.forecastfm.core.ui.components.weather.WeatherDetailsCard
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.emirgasic.forecastfm.feature.weather.WeatherViewModel

@Composable
fun WeatherScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = viewModel()
) {

    val weather by viewModel.weather.collectAsState()
    val hourlyForecast by viewModel.hourlyForecast.collectAsState()
    val dailyForecast by viewModel.dailyForecast.collectAsState()
    val uiState by viewModel.uiState.collectAsState()


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 60.dp,
                start = 10.dp,
                bottom = 10.dp,
                end = 10.dp
            )
    ) {

        when (uiState) {

            WeatherUiState.LOADING -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }


            WeatherUiState.ERROR -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Unable to load weather",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Please check your connection and try again.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.loadWeather()
                        }
                    ) {

                        Text("Retry")
                    }
                }
            }


            WeatherUiState.SUCCESS -> {

                weather?.let { currentWeather ->

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {

                        item {

                            SectionTitle(
                                title = "Today's Weather"
                            )
                        }


                        item {

                            CurrentWeatherCard(
                                weatherIcon = painterResource(
                                    currentWeather.icon
                                ),
                                temperature = currentWeather.temperature,
                                condition = currentWeather.condition,
                                location = currentWeather.location,
                                updated = "Updated just now"
                            )
                        }


                        item {

                            WeatherDetailsCard(
                                feelsLike = currentWeather.feelsLike,
                                humidity = currentWeather.humidity,
                                wind = currentWeather.wind,
                                uvIndex = currentWeather.uvIndex,
                                airQuality = currentWeather.airQuality
                            )
                        }


                        item {

                            SectionTitle(
                                title = "Hourly Forecast"
                            )
                        }


                        item {

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline
                                )
                            ) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {

                                    hourlyForecast.forEach {

                                        ForecastRowItem(
                                            title = it.time,
                                            icon = painterResource(it.icon),
                                            temperature = it.temperature
                                        )
                                    }
                                }
                            }
                        }


                        item {

                            SectionTitle(
                                title = "Next 5 Days"
                            )
                        }


                        item {

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline
                                )
                            ) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {

                                    dailyForecast.forEach {

                                        ForecastRowItem(
                                            title = it.time,
                                            icon = painterResource(it.icon),
                                            temperature = it.temperature
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}