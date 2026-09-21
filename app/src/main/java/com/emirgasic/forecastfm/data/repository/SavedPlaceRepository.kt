package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.place.SavedPlaceApi

class SavedPlaceRepository(
    private val savedPlaceApi: SavedPlaceApi = SavedPlaceApi()
) {

    suspend fun savePlace(placeId: String, userId: String) {
        savedPlaceApi.savePlace(placeId, userId)
    }

    suspend fun unsavePlace(placeId: String, userId: String) {
        savedPlaceApi.unsavePlace(placeId, userId)
    }

    suspend fun isPlaceSaved(placeId: String, userId: String): Boolean {
        return savedPlaceApi.isPlaceSaved(placeId, userId)
    }

    suspend fun getSavedPlaces(userId: String): List<Place> {
        val responses = savedPlaceApi.getSavedPlaces(userId)
        return responses.map { response ->
            val fullImageUrl = if (
                !response.imageUrl.isNullOrBlank() &&
                (response.imageUrl.startsWith("http://") || response.imageUrl.startsWith("https://"))
            ) {
                response.imageUrl
            } else if (!response.imageUrl.isNullOrBlank()) {
                "${ApiClient.baseUrl()}${response.imageUrl}"
            } else {
                null
            }

            Place(
                id = response.id,
                name = response.name,
                category = response.category,
                venueId = response.venueId,
                address = response.address,
                latitude = response.latitude,
                longitude = response.longitude,
                description = response.description,
                imageUrl = fullImageUrl,
                rating = response.rating
            )
        }
    }
}