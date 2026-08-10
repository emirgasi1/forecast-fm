package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Profile
import com.emirgasic.forecastfm.data.model.ProfilePost

class ProfileRepository {

    fun getProfile(): Profile {

        return Profile(

            username = "Emir",

            bio = "Coffee, music & Sarajevo",

            profileImage = R.drawable.profile_picture,

            likes = 24,

            saved = 19,

            posts = 4,

            favoritePlaylists = listOf(
                "Chill Pop",
                "Sarajevo Nights"
            ),

            profilePosts = listOf(

                ProfilePost(
                    id = "1",
                    image = R.drawable.outfit1,
                    caption = "Morning coffee"
                ),

                ProfilePost(
                    id = "2",
                    image = R.drawable.outfit2,
                    caption = "Rainy walk"
                ),

                ProfilePost(
                    id = "3",
                    image = R.drawable.outfit3,
                    caption = "Night vibes"
                ),

                ProfilePost(
                    id = "4",
                    image = R.drawable.outfit1,
                    caption = "Sunny afternoon"
                )

            )

        )

    }

}