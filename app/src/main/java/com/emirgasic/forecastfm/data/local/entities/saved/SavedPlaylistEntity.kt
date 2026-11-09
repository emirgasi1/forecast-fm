package com.emirgasic.forecastfm.data.local.entities.saved

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_playlists")
data class SavedPlaylistEntity(
    @PrimaryKey val playlistId: String,
    val savedAt: Long = System.currentTimeMillis()
)