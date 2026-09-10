package com.emirgasic.forecastfm.network.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    val bio: String? = null,
    val profileImageUrl: String? = null
)