package com.emirgasic.forecastfm.data.model

data class Outfit(
    val id: String,
    val imageUrl: String,
    val title: String,
    val weatherCondition: String,
    val season: String,
    val likes: Int = 0,
    val storeName: String? = null,
    val storeAddress: String? = null,
    val price: String? = null
)

data class Style(
    val outfits: List<Outfit>
)