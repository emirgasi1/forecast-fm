package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Profile
import com.emirgasic.forecastfm.data.model.ProfilePost
import com.emirgasic.forecastfm.network.post.PostApi
import com.emirgasic.forecastfm.network.profile.ProfileApi

class ProfileRepository(
    private val profileApi: ProfileApi
) {

    suspend fun getProfile(): Profile {

        val response =
            profileApi.getProfile(
                "d0bb864c-e207-4b4e-a0a7-102d10322ee3"
            )

        val profilePosts =
            response.posts.map { post ->

                ProfilePost(
                    id = post.id,
                    imageUrl = post.imageUrl,
                    caption = post.caption ?: ""
                )
            }

        return Profile(

            username = response.username,

            bio = response.bio ?: "",

            profileImage = R.drawable.profile_picture,

            likes = response.likes,

            saved = response.saved,

            posts = response.posts.size,

            favoritePlaylists =
                response.favoritePlaylists.map { playlist ->
                    playlist.title
                },

            profilePosts = profilePosts
        )
    }
}