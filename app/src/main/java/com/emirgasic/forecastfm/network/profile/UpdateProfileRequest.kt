package com.emirgasic.forecastfm.network.profile

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val username: String,
    val bio: String? = null,
    val favoriteLocation: String? = null
)