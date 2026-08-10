package com.emirgasic.forecastfm.data.model

data class Home(
    val greeting: String,
    val weather: Weather,
    val forecast: List<Forecast>,
    val playlists: List<Playlist>
)