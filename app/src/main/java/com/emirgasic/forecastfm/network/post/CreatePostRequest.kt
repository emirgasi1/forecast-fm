package com.emirgasic.forecastfm.network.post

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequest(
    val userId: String,
    val caption: String?,
    val imageUrl: String?
)