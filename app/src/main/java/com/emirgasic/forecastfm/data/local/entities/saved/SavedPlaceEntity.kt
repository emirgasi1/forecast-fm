package com.emirgasic.forecastfm.data.local.entities.saved

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_places")
data class SavedPlaceEntity(
    @PrimaryKey val placeId: String,
    val savedAt: Long = System.currentTimeMillis()
)