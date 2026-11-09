package com.emirgasic.forecastfm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherData(
    val weather: Weather,
    val hourly: List<Forecast>,
    val daily: List<Forecast>
)