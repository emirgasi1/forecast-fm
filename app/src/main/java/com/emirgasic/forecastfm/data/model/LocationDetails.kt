package com.emirgasic.forecastfm.data.model

data class LocationDetails(
    val location: String,
    val description: String,

    val weatherIcon: Int,
    val temperature: String,
    val condition: String,
    val humidity: String,
    val wind: String,

    val playlistTitle: String,
    val songs: List<String>,

    val outfitTitle: String,
    val outfitDescription: String
)