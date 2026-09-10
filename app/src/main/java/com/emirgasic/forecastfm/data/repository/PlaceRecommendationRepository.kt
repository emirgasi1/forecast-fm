package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.model.PlaceRecommendation
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.placerecommendation.PlaceRecommendationApi

class PlaceRecommendationRepository(
    private val api: PlaceRecommendationApi = PlaceRecommendationApi()
) {

    suspend fun getRecommendations(
        category: String? = null,
        suitableFor: String? = null,
        weatherCondition: String? = null,
        ageGroup: String? = null
    ): List<PlaceRecommendation> {
        val responses = api.getRecommendations(
            category = category,
            suitableFor = suitableFor,
            weatherCondition = weatherCondition,
            ageGroup = ageGroup
        )

        return responses.map { response ->
            PlaceRecommendation(
                id = response.id,
                name = response.name,
                category = response.category,
                location = response.location,
                description = response.description,
                suitableFor = response.suitableFor,
                weatherCondition = response.weatherCondition,
                ageGroup = response.ageGroup,
                rating = response.rating,
                imageUrl = if (response.imageUrl != null) {
                    "${ApiClient.baseUrl()}${response.imageUrl}"
                } else null,
                address = response.address
            )
        }
    }

    suspend fun getRecommendationById(id: String): PlaceRecommendation? {
        return try {
            val response = api.getRecommendationById(id)
            PlaceRecommendation(
                id = response.id,
                name = response.name,
                category = response.category,
                location = response.location,
                description = response.description,
                suitableFor = response.suitableFor,
                weatherCondition = response.weatherCondition,
                ageGroup = response.ageGroup,
                rating = response.rating,
                imageUrl = if (response.imageUrl != null) {
                    "${ApiClient.baseUrl()}${response.imageUrl}"
                } else null
            )
        } catch (e: Exception) {
            null
        }
    }
}