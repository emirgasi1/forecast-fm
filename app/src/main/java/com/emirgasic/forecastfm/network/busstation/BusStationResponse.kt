package com.emirgasic.forecastfm.network.busstation

import kotlinx.serialization.Serializable

@Serializable
data class BusStationResponse(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val lines: List<String>,
    val createdAt: String
)