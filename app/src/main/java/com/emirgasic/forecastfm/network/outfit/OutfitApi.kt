package com.emirgasic.forecastfm.network.outfit

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class OutfitApi {

    suspend fun getTrendingOutfits(): List<OutfitResponse> {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/outfits/trending").body()
    }

    suspend fun getOutfitsByWeather(weather: String): List<OutfitResponse> {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/outfits/weather/$weather").body()
    }

    suspend fun likeOutfit(outfitId: String) {
        ApiClient.client.post("${ApiClient.baseUrl()}/api/outfits/$outfitId/like") {
            setBody(emptyMap<String, String>())
        }
    }

    suspend fun saveOutfit(outfitId: String, userId: String) {
        ApiClient.client.post("${ApiClient.baseUrl()}/api/outfits/$outfitId/save") {
            header("User-Id", userId)
        }
    }

    suspend fun unsaveOutfit(outfitId: String, userId: String) {
        ApiClient.client.delete("${ApiClient.baseUrl()}/api/outfits/$outfitId/save") {
            header("User-Id", userId)
        }
    }

    suspend fun isOutfitSaved(outfitId: String, userId: String): Boolean {
        val response: Map<String, Boolean> = ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/outfits/$outfitId/save"
        ) {
            header("User-Id", userId)
        }.body()
        return response["saved"] ?: false
    }

    suspend fun getSavedOutfits(userId: String): List<OutfitResponse> {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/outfits/saved") {
            header("User-Id", userId)
        }.body()
    }
    suspend fun getOutfitById(id: String): OutfitResponse {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/outfits/$id").body()
    }
}