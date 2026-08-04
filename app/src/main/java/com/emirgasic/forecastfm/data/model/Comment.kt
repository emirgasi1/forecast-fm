package com.emirgasic.forecastfm.data.model

data class Comment(
    val id: String,
    val user: User,
    val text: String,
    val time: String,
    val likes: Int
)