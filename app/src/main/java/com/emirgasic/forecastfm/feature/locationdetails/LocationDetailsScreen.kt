package com.emirgasic.forecastfm.feature.locationdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
fun LocationDetailsScreen(
    navController: NavController,
    locationId: String?,
    modifier: Modifier = Modifier,
    viewModel: LocationDetailsViewModel = viewModel()
) {

    val locationDetails by viewModel.locationDetails.collectAsState()
    val outfits by viewModel.outfits.collectAsState()

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                LocationHeader(
                    location = details.location.name,
                    description = details.location.description
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
                if (details.playlist == null) {
                    Text(
                        text = "No playlist found",
                        style = MaterialTheme.typography.bodyLarge
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

            // Outfits Section
            item {
                Text(
                    text = "Outfits for this weather",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            items(outfits) { outfit ->
                LocationOutfitCard(
                    imageUrl = outfit.imageUrl,
                    title = outfit.title,
                    weatherCondition = outfit.weatherCondition,
                    season = outfit.season
                )
            }

            // Place Discovery Button
            item {
                PlaceDiscoveryCard(
                    location = details.location.name,
                    weatherIcon = painterResource(details.weather.icon),
                    weather = details.weather.condition,
                    temperature = details.weather.temperature,
                    playlist = details.playlist?.title ?: "No playlist",
                    outfit = outfits.firstOrNull()?.title ?: "No outfit",
                    onChooseClick = {
                        navController.navigate(Routes.PlaceRecommendation)
                    }
                )
            }
        }
    }
}