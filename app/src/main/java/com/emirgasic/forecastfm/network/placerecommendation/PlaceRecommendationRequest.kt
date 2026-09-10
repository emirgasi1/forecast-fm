package com.emirgasic.forecastfm.network.placerecommendation

import kotlinx.serialization.Serializable

@Serializable
data class PlaceRecommendationRequest(
    val category: String? = null,
    val suitableFor: String? = null,
    val weatherCondition: String? = null,
    val ageGroup: String? = null
)