package com.emirgasic.forecastfm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FeedPost(
    val id: String,
    val user: User,
    val image: String,
    val caption: String,
    val weather: Weather,
    val playlist: Playlist? = null,
    val outfitId: String? = null,
    val outfitTitle: String? = null,
    val time: String,
    val likes: Int,
    val comments: Int
)