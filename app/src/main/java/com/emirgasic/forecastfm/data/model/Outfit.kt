package com.emirgasic.forecastfm.data.model

data class Outfit(
    val id: String,
    val image: Int,
    val title: String,
    val weatherCondition: String,
    val season: String
)