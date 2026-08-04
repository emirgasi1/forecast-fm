package com.emirgasic.forecastfm.data.repository


import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.User

class UserRepository {

    fun getCurrentUser(): User {
        return User(
            id = "1",
            username = "Emir",
            bio = "Coffee, music & Sarajevo vibes ☕",
            profileImage = R.drawable.outfit2,
            favoriteLocation = "Baščaršija",
            likes = 248,
            posts = 32,
            saved = 14
        )
    }
}