package com.emirgasic.forecastfm.data.model

data class BusStation(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val lines: List<String>
)