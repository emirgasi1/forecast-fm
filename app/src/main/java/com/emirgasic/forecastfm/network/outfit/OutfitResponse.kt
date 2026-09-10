package com.emirgasic.forecastfm.network.outfit

import kotlinx.serialization.Serializable

@Serializable
data class OutfitResponse(
    val id: String,
    val userId: String,
    val imageUrl: String,
    val title: String,
    val weatherCondition: String,
    val season: String,
    val likes: Int = 0,
    val storeName: String? = null,
    val storeAddress: String? = null,
    val price: String? = null,
    val createdAt: String
)