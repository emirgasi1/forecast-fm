package com.emirgasic.forecastfm.network.place

import kotlinx.serialization.Serializable

@Serializable
data class PlaceResponse(
    val id: String,
    val name: String,
    val category: String,
    val venueId: String? = null,
    val address: String? = null,
    val latitude: Double,
    val longitude: Double,
    val description: String? = null,
    val imageUrl: String? = null,
    val openingHours: String? = null,
    val outdoorSeating: Boolean = false,
    val createdAt: String
)


@Serializable
data class PlaceCountResponse(val count: Int)