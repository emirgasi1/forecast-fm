package com.emirgasic.forecastfm.network.placerecommendation

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PlaceRecommendationApi {

    suspend fun getRecommendations(
        category: String? = null,
        suitableFor: String? = null,
        weatherCondition: String? = null,
        ageGroup: String? = null
    ): List<PlaceRecommendationResponse> {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/place-recommendations") {
            parameter("category", category)
            parameter("suitableFor", suitableFor)
            parameter("weatherCondition", weatherCondition)
            parameter("ageGroup", ageGroup)
        }.body()
    }

    suspend fun getRecommendationById(id: String): PlaceRecommendationResponse {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/place-recommendations/$id").body()
    }
}