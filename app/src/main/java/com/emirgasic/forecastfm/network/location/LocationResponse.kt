package com.emirgasic.forecastfm.network.location

import kotlinx.serialization.Serializable

@Serializable
data class LocationResponse(
    val id: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val category: String = "general",
    val address: String? = null,
    val openingHours: String? = null,
    val outdoorSeating: Boolean = false
)