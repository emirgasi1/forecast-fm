package com.emirgasic.forecastfm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfilePost(
    val id: String,
    val imageUrl: String?,
    val caption: String
)