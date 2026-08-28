package com.emirgasic.forecastfm.network.musichistory

import kotlinx.serialization.Serializable

@Serializable
data class AddMusicHistoryRequest(
    val userId: String,
    val playlistId: String
)