package com.emirgasic.forecastfm.network.playlist

import com.emirgasic.forecastfm.network.music.MusicResponse
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistResponse(
    val id: String,
    val title: String,
    val genre: String,
    val mood: String,
    val albumImageUrl: String?,
    val weather: String,
    val temperature: String,
    val location: String,
    val songs: List<MusicResponse>,
    val likes: Int,
    val spotifyUrl: String?,
    val youtubeUrl: String?
)