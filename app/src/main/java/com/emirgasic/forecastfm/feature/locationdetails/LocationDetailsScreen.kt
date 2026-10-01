package com.emirgasic.forecastfm.feature.locationdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.locationdetails.LocationHeader
import com.emirgasic.forecastfm.core.ui.components.locationdetails.LocationMusicCard
import com.emirgasic.forecastfm.core.ui.components.locationdetails.LocationOutfitCard
import com.emirgasic.forecastfm.core.ui.components.locationdetails.LocationWeatherCard
import com.emirgasic.forecastfm.core.ui.components.locationdetails.PlaceDiscoveryCard
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun LocationDetailsScreen(
    navController: NavController,
    locationId: String?,
    modifier: Modifier = Modifier,
    viewModel: LocationDetailsViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return LocationDetailsViewModel(
                    locationRepository = LocationRepository(),
                    weatherRepository = WeatherRepository(),
                    playlistRepository = PlaylistRepository(PlaylistApi()),
                    outfitRepository = OutfitRepository(),
                    placeRepository = PlaceRepository()
                ) as T
            }
        }
    )
) {

    val locationDetails by viewModel.locationDetails.collectAsState()
    val outfits by viewModel.outfits.collectAsState()
    val placesCount by viewModel.placesCount.collectAsState()

    val forecastColors = LocalForecastColors.current

    LaunchedEffect(locationId) {
        locationId?.let {
            viewModel.loadLocation(it)
        }
    }

    val details = locationDetails ?: return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 60.dp,
                start = 10.dp,
                end = 10.dp,
                bottom = 10.dp
            )
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.navigationBarsPadding()
        ) {

            item {
                LocationHeader(
                    location = details.location.name,
                    description = details.location.description
                )
            }

            item {
                Text(
                    text = "Current weather",
                    style = MaterialTheme.typography.titleLarge,
                    color = forecastColors.title
                )
            }

            item {
                LocationWeatherCard(
                    weatherIcon = painterResource(details.weather.icon),
                    condition = details.weather.condition,
                    temperature = details.weather.temperature,
                    humidity = details.weather.humidity,
                    wind = details.weather.wind
                )
            }

            item {
                Text(
                    text = "Today's Soundtrack",
                    style = MaterialTheme.typography.titleLarge,
                    color = forecastColors.title
                )
            }

            item {
                if (details.playlist == null) {
                    Text(
                        text = "No playlist found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = forecastColors.muted
                    )
                } else {
                    LocationMusicCard(
                        playlistTitle = details.playlist.title,
                        songs = details.playlist.songs.map { song ->
                            "${song.title} - ${song.artist}"
                        }
                    )
                }
            }

            item {
                Text(
                    text = "Outfits for this weather",
                    style = MaterialTheme.typography.titleLarge,
                    color = forecastColors.title
                )
            }

            items(outfits.take(2)) { outfit ->
                LocationOutfitCard(
                    imageUrl = outfit.imageUrl,
                    title = outfit.title,
                    weatherCondition = outfit.weatherCondition,
                    season = outfit.season
                )
            }

            item {
                PlaceDiscoveryCard(
                    location = details.location.name,
                    weatherIcon = painterResource(details.weather.icon),
                    weather = details.weather.condition,
                    temperature = details.weather.temperature,
                    playlist = details.playlist?.title ?: "No playlist",
                    outfit = outfits.firstOrNull()?.title ?: "No outfit",
                    placesCount = placesCount,
                    onChooseClick = {
                        navController.navigate(Routes.placeRecommendationRoute(details.location.id))
                    }
                )
            }
        }
    }
}