package com.emirgasic.forecastfm.data.model

data class Profile(
    val username: String,
    val bio: String,
    val profileImage: Int,

    val likes: Int,
    val saved: Int,
    val posts: Int,

    val favoritePlaylists: List<String>,
    val profilePosts: List<ProfilePost>
)