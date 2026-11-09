package com.emirgasic.forecastfm.data.local.entities.place

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val venueId: String?,
    val address: String?,
    val latitude: Double,
    val longitude: Double,
    val description: String?,
    val imageUrl: String?,
    val openingHours: String?,
    val outdoorSeating: Boolean
)