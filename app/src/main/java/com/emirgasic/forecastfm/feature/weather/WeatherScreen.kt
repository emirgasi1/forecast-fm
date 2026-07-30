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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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


@Composable
fun WeatherScreen(
    navController: NavController,
    modifier: Modifier = Modifier
){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 60.dp,
                start = 10.dp,
                bottom = 10.dp,
                end = 10.dp
            )
    ){

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ){


            item {

                SectionTitle(
                    title = "Today's Weather"
                )
            }


            item {

                CurrentWeatherCard(
                    weatherIcon = painterResource(R.drawable.sun),
                    temperature = "22°C",
                    condition = "Sunny",
                    location = "Baščaršija",
                    updated = "Updated 5 minutes ago"
                )
            }



            item {

                WeatherDetailsCard(
                    feelsLike = "21°C",
                    humidity = "55%",
                    wind = "8 km/h",
                    uvIndex = "Moderate",
                    airQuality = "Good"
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
                ){

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ){

                        ForecastRowItem(
                            title = "10:00 AM",
                            icon = painterResource(R.drawable.sun),
                            temperature = "22°C"
                        )

                        ForecastRowItem(
                            title = "11:00 AM",
                            icon = painterResource(R.drawable.sun),
                            temperature = "23°C"
                        )

                        ForecastRowItem(
                            title = "12:00 PM",
                            icon = painterResource(R.drawable.sunny_cloudy),
                            temperature = "24°C"
                        )

                        ForecastRowItem(
                            title = "01:00 PM",
                            icon = painterResource(R.drawable.sunny_cloudy),
                            temperature = "25°C"
                        )

                        ForecastRowItem(
                            title = "02:00 PM",
                            icon = painterResource(R.drawable.sun),
                            temperature = "25°C"
                        )

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
                ){

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ){

                        ForecastRowItem(
                            title = "Monday",
                            icon = painterResource(R.drawable.sun),
                            temperature = "22°C"
                        )


                        ForecastRowItem(
                            title = "Tuesday",
                            icon = painterResource(R.drawable.sunny_cloudy),
                            temperature = "21°C"
                        )


                        ForecastRowItem(
                            title = "Wednesday",
                            icon = painterResource(R.drawable.heavy_rain),
                            temperature = "18°C"
                        )


                        ForecastRowItem(
                            title = "Thursday",
                            icon = painterResource(R.drawable.sun),
                            temperature = "23°C"
                        )


                        ForecastRowItem(
                            title = "Friday",
                            icon = painterResource(R.drawable.sunny_cloudy),
                            temperature = "24°C"
                        )

                    }
                }
            }
        }
    }
}