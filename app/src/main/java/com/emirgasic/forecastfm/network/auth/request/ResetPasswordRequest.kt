package com.emirgasic.forecastfm.network.auth.request


import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)