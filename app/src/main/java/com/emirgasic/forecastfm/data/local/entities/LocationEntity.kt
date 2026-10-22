package com.emirgasic.forecastfm.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val address: String?,
    val openingHours: String?,
    val outdoorSeating: Boolean
)