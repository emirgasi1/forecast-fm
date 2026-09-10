package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.place.PlaceApi

class PlaceRepository(
    private val placeApi: PlaceApi = PlaceApi()
) {

    suspend fun getPlacesByVenue(venueId: String): List<Place> {
        val responses = placeApi.getPlacesByVenue(venueId)
        return responses.map { response ->
            Place(
                id = response.id,
                name = response.name,
                category = response.category,
                venueId = response.venueId,
                address = response.address,
                latitude = response.latitude,
                longitude = response.longitude,
                description = response.description,
                imageUrl = if (response.imageUrl != null) {
                    "${ApiClient.baseUrl()}${response.imageUrl}"
                } else null,
                rating = response.rating
            )
        }
    }

    suspend fun getPlaceById(id: String): Place? {
        return try {
            val response = placeApi.getPlaceById(id)
            Place(
                id = response.id,
                name = response.name,
                category = response.category,
                venueId = response.venueId,
                address = response.address,
                latitude = response.latitude,
                longitude = response.longitude,
                description = response.description,
                imageUrl = if (response.imageUrl != null) {
                    "${ApiClient.baseUrl()}${response.imageUrl}"
                } else null,
                rating = response.rating
            )
        } catch (e: Exception) {
            null
        }
    }
}