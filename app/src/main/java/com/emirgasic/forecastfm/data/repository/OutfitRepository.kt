package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.core.utils.resolveImageUrlOr
import com.emirgasic.forecastfm.data.local.datastore.OutfitCacheManager
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.network.outfit.OutfitApi

class OutfitRepository(
    private val outfitApi: OutfitApi = OutfitApi(),
    private val cacheManager: OutfitCacheManager? = null // 👈 Added Cache Manager
) {

    suspend fun getTrendingOutfits(): List<Outfit> {
        return try {
            val responses = outfitApi.getTrendingOutfits()
            val outfits = responses.map { response -> response.toOutfit() }

            // Save to DataStore
            cacheManager?.saveOutfits(outfits)

            outfits
        } catch (e: Exception) {
            // Offline fallback
            val cached = cacheManager?.getCachedOutfits()
            if (!cached.isNullOrEmpty()) {
                cached
            } else {
                throw Exception("No internet and no cached outfits available")
            }
        }
    }

    suspend fun getOutfitsByWeather(weather: String): List<Outfit> {
        return try {
            val responses = outfitApi.getOutfitsByWeather(weather)
            val outfits = responses.map { response -> response.toOutfit() }

            // If we fetch weather-specific outfits, we can cache them too
            // (Though caching all trending is usually enough)

            outfits
        } catch (e: Exception) {
            // If offline, just filter the cached trending outfits by weather
            val cached = cacheManager?.getCachedOutfits()
            if (!cached.isNullOrEmpty()) {
                cached.filter { it.weatherCondition.equals(weather, ignoreCase = true) }
            } else {
                emptyList() // Return empty instead of crashing the UI
            }
        }
    }

    suspend fun getOutfitById(id: String): Outfit {
        return try {
            outfitApi.getOutfitById(id).toOutfit()
        } catch (e: Exception) {
            // Try to find it in the cache
            val cached = cacheManager?.getCachedOutfits()
            val cachedOutfit = cached?.firstOrNull { it.id == id }

            if (cachedOutfit != null) {
                cachedOutfit
            } else {
                throw Exception("Outfit not found offline")
            }
        }
    }

    suspend fun getGroupedOutfits(): Map<String, List<Outfit>> {
        return try {
            val responses = outfitApi.getGroupedOutfits()
            responses.mapValues { (_, outfitResponses) ->
                outfitResponses.map { it.toOutfit() }
            }
        } catch (e: Exception) {
            val cached = cacheManager?.getCachedOutfits() ?: emptyList()
            cached.groupBy { it.weatherCondition }
        }
    }

    suspend fun likeOutfit(outfitId: String) {
        try {
            outfitApi.likeOutfit(outfitId)
        } catch (_: Exception) {
        }
    }

    private fun com.emirgasic.forecastfm.network.outfit.OutfitResponse.toOutfit(): Outfit {

        val cleanUrl = imageUrl.replace("\\u0026", "&")

        val fullImageUrl = resolveImageUrlOr(cleanUrl, cleanUrl)

        android.util.Log.d("ImageDebug", "Loading Outfit Image: $fullImageUrl")

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