package com.emirgasic.forecastfm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Forecast(
    val time: String,
    val icon: Int,
    val temperature: String
)