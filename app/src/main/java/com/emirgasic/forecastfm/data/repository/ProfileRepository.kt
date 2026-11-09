package com.emirgasic.forecastfm.data.repository

import android.content.ContentResolver
import com.emirgasic.forecastfm.core.utils.resolveImageUrlOr
import com.emirgasic.forecastfm.data.local.datastore.ProfileCacheManager
import com.emirgasic.forecastfm.data.model.Profile
import com.emirgasic.forecastfm.data.model.ProfilePost
import com.emirgasic.forecastfm.network.profile.ProfileApi
import com.emirgasic.forecastfm.network.profile.UpdateProfileRequest

class ProfileRepository(
    private val profileApi: ProfileApi,
    private val cacheManager: ProfileCacheManager? = null
) {

    // 1. Read from DataStore (Instant)
    suspend fun getLocalProfile(): Profile? {
        return cacheManager?.getCachedProfile()
    }

    // 2. Fetch from Network & Update Cache
    suspend fun getProfile(userId: String): Profile {
        val response = profileApi.getProfile(userId)

        val profilePosts = response.posts.map { post ->
            ProfilePost(
                id = post.id,
                imageUrl = resolveImageUrlOr(
                    post.imageUrl,
                    "https://picsum.photos/seed/${post.id}/400/400"
                ),
                caption = post.caption ?: ""
            )
        }

        val profile = Profile(
            username = response.username,
            bio = response.bio ?: "",
            profileImage = resolveImageUrlOr(
                response.profileImageUrl,
                "https://picsum.photos/seed/${response.username}/400/400"
            ),
            likes = response.likes,
            saved = response.saved,
            posts = response.posts.size,
            favoritePlaylists = response.favoritePlaylists.map { playlist ->
                playlist.title
            },
            profilePosts = profilePosts
        )

        // Save to DataStore
        cacheManager?.saveProfile(profile)

        return profile
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