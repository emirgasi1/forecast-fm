package com.emirgasic.forecastfm.network.auth.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)