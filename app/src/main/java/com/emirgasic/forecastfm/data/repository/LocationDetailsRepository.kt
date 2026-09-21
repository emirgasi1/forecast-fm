package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.network.location.LocationApi
import com.emirgasic.forecastfm.network.location.LocationResponse

class LocationRepository(
    private val api: LocationApi = LocationApi()
) {

    suspend fun getLocations(): List<LocationResponse> {
        return api.getLocations()
    }

    suspend fun getLocationById(id: String): LocationResponse? {
        return try {
            api.getLocations().firstOrNull { it.id == id }
        } catch (e: Exception) {
            null
        }
    }
}