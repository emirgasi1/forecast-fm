package com.emirgasic.forecastfm.network.musichistory

import kotlinx.serialization.Serializable

@Serializable
data class MusicHistoryResponse(
    val id: String,
    val playlistId: String,
    val title: String,
    val weather: String,
    val temperature: String,
    val location: String,
    val playedAt: String
)