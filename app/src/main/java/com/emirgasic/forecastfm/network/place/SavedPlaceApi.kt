package com.emirgasic.forecastfm.network.place

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class SavedPlaceApi {

    suspend fun savePlace(placeId: String, userId: String) {
        ApiClient.client.post("${ApiClient.baseUrl()}/api/places/$placeId/save") {
            header("User-Id", userId)
            setBody(emptyMap<String, String>())
        }
    }

    suspend fun unsavePlace(placeId: String, userId: String) {
        ApiClient.client.delete("${ApiClient.baseUrl()}/api/places/$placeId/save") {
            header("User-Id", userId)
        }
    }

    suspend fun isPlaceSaved(placeId: String, userId: String): Boolean {
        val response: Map<String, Boolean> = ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/places/$placeId/save"
        ) {
            header("User-Id", userId)
        }.body()
        return response["saved"] ?: false
    }

    suspend fun getSavedPlaces(userId: String): List<PlaceResponse> {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/places/saved") {
            header("User-Id", userId)
        }.body()
    }
}