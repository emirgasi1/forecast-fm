package com.emirgasic.forecastfm.feature.weather

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.ui.components.common.LoadingScreen
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.weather.CurrentWeatherCard
import com.emirgasic.forecastfm.core.ui.components.weather.ForecastRowItem
import com.emirgasic.forecastfm.core.ui.components.weather.WeatherDetailsCard
import com.emirgasic.forecastfm.data.recommender.HomeRecommender
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import com.emirgasic.forecastfm.feature.map.LocationManager
import com.emirgasic.forecastfm.network.location.LocationResponse
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun WeatherScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WeatherViewModel(
                    repository = WeatherRepository()
                ) as T
            }
        }
    )
) {
    val context = LocalContext.current

    val locationRepository = remember { LocationRepository() }
    val locationManager = remember { LocationManager(context) }

    var location by remember { mutableStateOf<LocationResponse?>(null) }

    val weather by viewModel.weather.collectAsState()
    val hourlyForecast by viewModel.hourlyForecast.collectAsState()
    val dailyForecast by viewModel.dailyForecast.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val forecastColors = LocalForecastColors.current

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locations = locationRepository.getLocations()
        if (locations.isEmpty()) return@LaunchedEffect

        location = if (fineGranted || coarseGranted) {
            val userLoc = locationManager.getCurrentLocation()
            HomeRecommender.pickNearestLocation(
                locations = locations,
                userLat = userLoc?.latitude,
                userLng = userLoc?.longitude
            )
        } else {
            locations.firstOrNull()
        }
    }

    LaunchedEffect(location) {
        location?.let {
            viewModel.loadWeather(
                location = it.name,
                latitude = it.latitude,
                longitude = it.longitude
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 60.dp,
                start = 10.dp,
                end = 10.dp
            )
    ) {

        when (uiState) {

            WeatherUiState.LOADING -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingScreen()
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
                        color = forecastColors.title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Please check your connection and try again.",
                        color = forecastColors.muted,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            location?.let {
                                viewModel.loadWeather(
                                    location = it.name,
                                    latitude = it.latitude,
                                    longitude = it.longitude
                                )
                            }
                        }
                    ) {
                        Text("Retry")
                    }
                }
            }

            WeatherUiState.SUCCESS -> {

                weather?.let { currentWeather ->

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.Start,
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {

                        item {
                            SectionTitle(title = "Today's Weather")
                        }

                        item {
                            CurrentWeatherCard(
                                weatherIcon = painterResource(currentWeather.icon),
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
                            SectionTitle(title = "Hourly Forecast")
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(
                                        elevation = 3.dp,
                                        shape = MaterialTheme.shapes.medium,
                                        ambientColor = forecastColors.shadow.copy(alpha = 0.25f),
                                        spotColor = forecastColors.shadow.copy(alpha = 0.4f)
                                    ),
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(
                                    containerColor = forecastColors.card
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                border = BorderStroke(1.dp, forecastColors.border)
                            ) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(forecastColors.card)
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            SectionTitle(title = "Next 5 Days")
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(
                                        elevation = 3.dp,
                                        shape = MaterialTheme.shapes.medium,
                                        ambientColor = forecastColors.shadow.copy(alpha = 0.25f),
                                        spotColor = forecastColors.shadow.copy(alpha = 0.4f)
                                    ),
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(
                                    containerColor = forecastColors.card
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                border = BorderStroke(1.dp, forecastColors.border)
                            ) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(forecastColors.card)
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
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