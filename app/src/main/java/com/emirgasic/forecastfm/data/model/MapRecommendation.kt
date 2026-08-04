package com.emirgasic.forecastfm.data.model

data class MapRecommendation(
    val location: String,
    val weatherIcon: Int,
    val temperature: String,
    val weather: String,
    val music: String,
    val outfit: String
)