package com.emirgasic.forecastfm.network.outfit

import kotlinx.serialization.Serializable

@Serializable
data class CreateOutfitRequest(
    val userId: String,
    val imageUrl: String,
    val title: String,
    val weatherCondition: String,
    val season: String,
    val storeName: String? = null,
    val storeAddress: String? = null,
    val price: String? = null,
    val storePhone: String? = null,
    val productUrl: String? = null
)