package com.emirgasic.forecastfm.network.comment


import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    val id: String,
    val userId: String,
    val postId: String,
    val text: String,
    val createdAt: String,
    val likes: Int
)