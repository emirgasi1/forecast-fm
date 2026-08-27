package com.emirgasic.forecastfm.data.model


data class Weather(
    val location: String,
    val temperature: String,
    val condition: String,
    val feelsLike: String,
    val humidity: String,
    val wind: String,
    val uvIndex: String,
    val airQuality: String,
    val icon: Int
)