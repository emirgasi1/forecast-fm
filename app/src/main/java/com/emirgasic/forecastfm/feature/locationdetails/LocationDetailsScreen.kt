package com.emirgasic.forecastfm.feature.locationdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
    location: String?,
    modifier: Modifier = Modifier,
    viewModel: LocationDetailsViewModel = viewModel()
) {

    val details by viewModel.locationDetails.collectAsState()

    LaunchedEffect(location) {

        location?.let {
            viewModel.loadLocation(it)
        }

    }

    val locationDetails = details ?: return


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
                    location = locationDetails.location,
                    description = locationDetails.description
                )

            }


            item {

                PlaceDiscoveryCard(

                    location = locationDetails.location,

                    weatherIcon = painterResource(
                        locationDetails.weatherIcon
                    ),

                    weather = locationDetails.condition,

                    temperature = locationDetails.temperature,

                    playlist = locationDetails.playlistTitle,

                    outfit = locationDetails.outfitDescription,

                    onChooseClick = {

                        navController.navigate(
                            Routes.PlaceRecommendation
                        )

                    }

                )

            }


            item {

                LocationWeatherCard(
                    weatherIcon = painterResource(
                        locationDetails.weatherIcon
                    ),

                    condition = locationDetails.condition,

                    temperature = locationDetails.temperature,

                    humidity = locationDetails.humidity,

                    wind = locationDetails.wind
                )

            }


            item {

                LocationMusicCard(
                    playlistTitle = locationDetails.playlistTitle,
                    songs = locationDetails.songs
                )

            }


            item {

                LocationOutfitCard(
                    outfitTitle = locationDetails.outfitTitle,
                    description = locationDetails.outfitDescription
                )

            }


        }

    }

}