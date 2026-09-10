package com.emirgasic.forecastfm.network.auth.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthUser(
    val id: String,
    val email: String,
    val username: String,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val favoriteLocation: String? = null,
    val isVerified: Boolean = false,
    val createdAt: String? = null,
    val lastLoginAt: String? = null,
    val status: String = "ACTIVE"
)