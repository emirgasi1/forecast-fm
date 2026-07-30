package com.emirgasic.forecastfm.data.model

data class Playlist(
    val id: String,
    val title: String,
    val genre: String,
    val mood: String,
    val albumImage: Int,
    val weather: String,
    val temperature: String,
    val location: String
)