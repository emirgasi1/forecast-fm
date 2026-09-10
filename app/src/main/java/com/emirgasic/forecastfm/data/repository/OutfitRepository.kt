package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.outfit.OutfitApi

class OutfitRepository(
    private val outfitApi: OutfitApi = OutfitApi()
) {

    suspend fun getTrendingOutfits(): List<Outfit> {
        val responses = outfitApi.getTrendingOutfits()
        return responses.map { response ->
            Outfit(
                id = response.id,
                imageUrl = "${ApiClient.baseUrl()}${response.imageUrl}",
                title = response.title,
                weatherCondition = response.weatherCondition,
                season = response.season,
                likes = response.likes,
                storeName = response.storeName,
                storeAddress = response.storeAddress,
                price = response.price
            )
        }
    }

    suspend fun getOutfitsByWeather(weather: String): List<Outfit> {
        val responses = outfitApi.getOutfitsByWeather(weather)
        return responses.map { response ->
            Outfit(
                id = response.id,
                imageUrl = "${ApiClient.baseUrl()}${response.imageUrl}",
                title = response.title,
                weatherCondition = response.weatherCondition,
                season = response.season,
                likes = response.likes,
                storeName = response.storeName,
                storeAddress = response.storeAddress,
                price = response.price
            )
        }
    }

    suspend fun getOutfitById(id: String): Outfit {
        val response = outfitApi.getOutfitById(id)
        return Outfit(
            id = response.id,
            imageUrl = "${ApiClient.baseUrl()}${response.imageUrl}",
            title = response.title,
            weatherCondition = response.weatherCondition,
            season = response.season,
            likes = response.likes,
            storeName = response.storeName,
            storeAddress = response.storeAddress,
            price = response.price
        )
    }

    suspend fun likeOutfit(outfitId: String) {
        outfitApi.likeOutfit(outfitId)
    }
}