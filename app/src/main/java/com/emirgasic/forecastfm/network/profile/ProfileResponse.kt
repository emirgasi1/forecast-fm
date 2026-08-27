package com.emirgasic.forecastfm.network.profile

import com.emirgasic.forecastfm.network.playlist.PlaylistResponse
import com.emirgasic.forecastfm.network.post.PostResponse
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: String,
    val username: String,
    val bio: String?,
    val profileImageUrl: String?,
    val favoriteLocation: String?,
    val likes: Int,
    val saved: Int,
    val posts: List<PostResponse>,
    val favoritePlaylists: List<PlaylistResponse>
)