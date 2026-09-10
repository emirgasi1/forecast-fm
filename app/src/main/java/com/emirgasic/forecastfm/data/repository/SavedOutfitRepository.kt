package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.network.outfit.OutfitApi
import com.emirgasic.forecastfm.network.outfit.OutfitResponse

class SavedOutfitRepository(
    private val outfitApi: OutfitApi = OutfitApi()
) {

    suspend fun saveOutfit(outfitId: String, userId: String) {
        outfitApi.saveOutfit(outfitId, userId)
    }

    suspend fun unsaveOutfit(outfitId: String, userId: String) {
        outfitApi.unsaveOutfit(outfitId, userId)
    }

    suspend fun isOutfitSaved(outfitId: String, userId: String): Boolean {
        return outfitApi.isOutfitSaved(outfitId, userId)
    }

    suspend fun getSavedOutfits(userId: String): List<OutfitResponse> {
        return outfitApi.getSavedOutfits(userId)
    }
}