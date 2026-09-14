package com.emirgasic.forecastfm.data.model

data class MapMarker(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: String,
    val category: String = "",
    val isSelected: Boolean = false
)