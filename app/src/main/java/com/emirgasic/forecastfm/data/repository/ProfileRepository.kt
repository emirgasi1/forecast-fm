package com.emirgasic.forecastfm.data.repository

import android.content.ContentResolver
import com.emirgasic.forecastfm.data.model.Profile
import com.emirgasic.forecastfm.data.model.ProfilePost
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.profile.ProfileApi
import com.emirgasic.forecastfm.network.profile.UpdateProfileRequest

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
            profileImage = if (!response.profileImageUrl.isNullOrBlank()) {
                "${ApiClient.baseUrl()}${response.profileImageUrl}"
            } else {
                "https://picsum.photos/seed/${response.username}/400/400"
            },
            likes = response.likes,
            saved = response.saved,
            posts = response.posts.size,
            favoritePlaylists = response.favoritePlaylists.map { playlist ->
                playlist.title
            },
            profilePosts = profilePosts
        )
    }

    suspend fun updateProfile(
        userId: String,
        username: String,
        bio: String,
        favoriteLocation: String
    ): Boolean {
        val request = UpdateProfileRequest(
            username = username,
            bio = bio,
            favoriteLocation = favoriteLocation
        )
        return profileApi.updateProfile(userId, request)
    }

    suspend fun uploadProfileImage(
        userId: String,
        contentResolver: ContentResolver,
        imageUri: String
    ): String? {
        return profileApi.uploadProfileImage(userId, contentResolver, imageUri)
    }
}