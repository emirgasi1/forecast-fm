package com.emirgasic.forecastfm.network.auth.response

import com.emirgasic.forecastfm.network.user.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val user: AuthUser,
    val expiresAt: Long
)