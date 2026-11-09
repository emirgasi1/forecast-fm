package com.emirgasic.forecastfm.data.local.entities.saved

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_outfits")
data class SavedOutfitEntity(
    @PrimaryKey val outfitId: String,
    val savedAt: Long = System.currentTimeMillis()
)