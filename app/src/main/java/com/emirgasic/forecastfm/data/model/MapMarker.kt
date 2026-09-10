package com.emirgasic.forecastfm.data.model

data class MapMarker(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: String, // "venue" or "place"
    val isSelected: Boolean = false
)