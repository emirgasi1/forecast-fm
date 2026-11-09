package com.emirgasic.forecastfm.data.local.entities.music

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: String,
    val playlistId: String,
    val title: String,
    val artist: String,
    val duration: Int,
    val albumImageUrl: String?
)