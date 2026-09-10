package com.emirgasic.forecastfm.data.model

import com.emirgasic.forecastfm.network.location.LocationResponse

data class LocationDetailsUi(
    val location: LocationResponse,
    val weather: Weather,
    val playlist: Playlist?
)