package com.emirgasic.forecastfm.network.auth.response

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val id: String,
    val email: String,
    val username: String,
    val bio: String?,
    val profileImageUrl: String?,
    val favoriteLocation: String?,
    val isVerified: Boolean,
    val status: String
)