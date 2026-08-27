package com.emirgasic.forecastfm.network.post

import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    val id: String,
    val userId: String,
    val caption: String?,
    val imageUrl: String?,
    val createdAt: String
)