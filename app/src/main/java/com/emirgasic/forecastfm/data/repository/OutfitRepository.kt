package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.core.utils.resolveImageUrl
import com.emirgasic.forecastfm.core.utils.resolveImageUrlOr
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.outfit.OutfitApi

class OutfitRepository(
    private val outfitApi: OutfitApi = OutfitApi()
) {

    suspend fun getTrendingOutfits(): List<Outfit> {
        val responses = outfitApi.getTrendingOutfits()
        return responses.map { response -> response.toOutfit() }
    }

    suspend fun getOutfitsByWeather(weather: String): List<Outfit> {
        val responses = outfitApi.getOutfitsByWeather(weather)
        return responses.map { response -> response.toOutfit() }
    }

    suspend fun getOutfitById(id: String): Outfit {
        return outfitApi.getOutfitById(id).toOutfit()
    }

    suspend fun likeOutfit(outfitId: String) {
        outfitApi.likeOutfit(outfitId)
    }

    private fun com.emirgasic.forecastfm.network.outfit.OutfitResponse.toOutfit(): Outfit {

        val fullImageUrl = resolveImageUrlOr(imageUrl, imageUrl)

        return Outfit(
            id = id,
            userId = userId,
            imageUrl = fullImageUrl,
            title = title,
            weatherCondition = weatherCondition,
            season = season,
            likes = likes,
            storeName = storeName,
            storeAddress = storeAddress,
            price = price,
            storePhone = storePhone,
            productUrl = productUrl,
            createdAt = createdAt
        )
    }
}