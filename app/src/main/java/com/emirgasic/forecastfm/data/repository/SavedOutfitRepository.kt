package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.local.daos.saved.SavedOutfitDao
import com.emirgasic.forecastfm.data.local.entities.saved.SavedOutfitEntity
import com.emirgasic.forecastfm.network.outfit.OutfitApi
import com.emirgasic.forecastfm.network.outfit.OutfitResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavedOutfitRepository(
    private val savedOutfitDao: SavedOutfitDao, // 👈 Room dependency
    private val outfitApi: OutfitApi = OutfitApi() // 👈 Network dependency
) {

    // --- ROOM LOCAL FUNCTIONS ---
    fun getSavedOutfitIds(): Flow<List<String>> {
        return savedOutfitDao.getSavedOutfits().map { list -> list.map { it.outfitId } }
    }

    fun isOutfitSavedLocally(outfitId: String): Flow<Boolean> {
        return savedOutfitDao.isOutfitSaved(outfitId)
    }

    // --- HYBRID SAVE/UNSAVE ---
    suspend fun toggleSaveOutfit(outfitId: String, userId: String, isCurrentlySaved: Boolean) {
        if (isCurrentlySaved) {
            savedOutfitDao.unsaveOutfit(outfitId)
            try { outfitApi.unsaveOutfit(outfitId, userId) } catch (_: Exception) {}
        } else {
            savedOutfitDao.saveOutfit(SavedOutfitEntity(outfitId))
            try { outfitApi.saveOutfit(outfitId, userId) } catch (_: Exception) {}
        }
    }

    // --- OLD NETWORK FUNCTIONS ---
    suspend fun getSavedOutfits(userId: String): List<OutfitResponse> {
        return outfitApi.getSavedOutfits(userId)
    }
}