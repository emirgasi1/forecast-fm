package com.emirgasic.forecastfm.network.auth.response

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val token: String,
    val refreshToken: String,
    val user: AuthUser,
    val expiresAt: Long
)