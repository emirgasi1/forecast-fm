package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.local.daos.saved.SavedPlaceDao
import com.emirgasic.forecastfm.data.local.entities.saved.SavedPlaceEntity
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.place.SavedPlaceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavedPlaceRepository(
    private val savedPlaceDao: SavedPlaceDao, // 👈 Room dependency
    private val savedPlaceApi: SavedPlaceApi = SavedPlaceApi() // 👈 Network dependency
) {

    // --- ROOM LOCAL FUNCTIONS ---
    fun getSavedPlaceIds(): Flow<List<String>> {
        return savedPlaceDao.getSavedPlaces().map { list -> list.map { it.placeId } }
    }

    fun isPlaceSavedLocally(placeId: String): Flow<Boolean> {
        return savedPlaceDao.isPlaceSaved(placeId)
    }

    // --- HYBRID SAVE/UNSAVE ---
    suspend fun toggleSavePlace(placeId: String, userId: String, isCurrentlySaved: Boolean) {
        if (isCurrentlySaved) {
            savedPlaceDao.unsavePlace(placeId)
            try { savedPlaceApi.unsavePlace(placeId, userId) } catch (_: Exception) {}
        } else {
            savedPlaceDao.savePlace(SavedPlaceEntity(placeId))
            try { savedPlaceApi.savePlace(placeId, userId) } catch (_: Exception) {}
        }
    }

    // --- OLD NETWORK FUNCTIONS ---
    suspend fun getSavedPlaces(userId: String): List<Place> {
        val responses = savedPlaceApi.getSavedPlaces(userId)
        return responses.map { response ->
            val fullImageUrl = if (!response.imageUrl.isNullOrBlank() && (response.imageUrl.startsWith("http"))) {
                response.imageUrl
            } else if (!response.imageUrl.isNullOrBlank()) {
                "${ApiClient.baseUrl()}${response.imageUrl}"
            } else null

            Place(
                id = response.id, name = response.name, category = response.category,
                venueId = response.venueId, address = response.address,
                latitude = response.latitude, longitude = response.longitude,
                description = response.description, imageUrl = fullImageUrl,
                openingHours = response.openingHours, outdoorSeating = response.outdoorSeating
            )
        }
    }
}