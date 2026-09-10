package com.emirgasic.forecastfm.network.place

import kotlinx.serialization.Serializable

@Serializable
data class PlaceResponse(
    val id: String,
    val name: String,
    val category: String,
    val venueId: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val imageUrl: String? = null,
    val rating: Double = 0.0,
    val createdAt: String
)