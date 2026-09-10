package com.emirgasic.forecastfm.network.music

import kotlinx.serialization.Serializable

@Serializable
data class MusicResponse(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Int,
    val albumImageUrl: String?
)