package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Profile
import com.emirgasic.forecastfm.data.model.ProfilePost
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.profile.ProfileApi

class ProfileRepository(
    private val profileApi: ProfileApi
) {

    suspend fun getProfile(userId: String): Profile {
        val response = profileApi.getProfile(userId)

        val profilePosts = response.posts.map { post ->
            ProfilePost(
                id = post.id,
                imageUrl = if (!post.imageUrl.isNullOrBlank()) {
                    "${ApiClient.baseUrl()}${post.imageUrl}"
                } else {
                    "https://picsum.photos/seed/${post.id}/400/400"
                },
                caption = post.caption ?: ""
            )
        }

        return Profile(
            username = response.username,
            bio = response.bio ?: "",
            profileImage = response.profileImageUrl ?: "",
            likes = response.likes,
            saved = response.saved,
            posts = response.posts.size,
            favoritePlaylists = response.favoritePlaylists.map { playlist ->
                playlist.title
            },
            profilePosts = profilePosts
        )
    }
}