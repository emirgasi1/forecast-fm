package com.emirgasic.forecastfm.data.model

data class Place(
    val id: String,
    val name: String,
    val category: String,
    val venueId: String?,
    val address: String?,
    val latitude: Double,
    val longitude: Double,
    val description: String?,
    val imageUrl: String?,
    val openingHours: String? = null,
    val outdoorSeating: Boolean = false
)