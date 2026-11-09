package com.emirgasic.forecastfm.data.local.entities.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val title: String,
    val genre: String,
    val mood: String,
    val albumImageUrl: String?,
    val weather: String,
    val temperature: String,
    val location: String,
    val likes: Int,
    val spotifyUrl: String?,
    val youtubeUrl: String?,
    val bestFor: String
)