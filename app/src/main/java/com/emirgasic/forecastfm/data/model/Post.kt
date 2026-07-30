package com.emirgasic.forecastfm.data.model

data class Post(
    val id: String,
    val user: User,
    val image: Int,
    val caption: String,
    val weather: Weather,
    val playlist: Playlist
)