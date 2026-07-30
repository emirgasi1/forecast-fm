package com.emirgasic.forecastfm.data.model

data class User(
    val id: String,
    val username: String,
    val bio: String,
    val profileImage: Int,
    val favoriteLocation: String,
    val likes: Int,
    val posts: Int,
    val saved: Int
)