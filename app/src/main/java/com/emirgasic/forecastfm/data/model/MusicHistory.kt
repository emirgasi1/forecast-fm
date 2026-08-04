package com.emirgasic.forecastfm.data.model

data class MusicHistory(
    val id: String,
    val section: String,
    val title: String,
    val weather: String,
    val temperature: String,
    val location: String,
    val time: String,
    val weatherIcon: Int
)