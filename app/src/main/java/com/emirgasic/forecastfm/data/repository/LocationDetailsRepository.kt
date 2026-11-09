package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.local.daos.location.LocationDao
import com.emirgasic.forecastfm.data.local.entities.location.LocationEntity
import com.emirgasic.forecastfm.network.location.LocationApi
import com.emirgasic.forecastfm.network.location.LocationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocationRepository(
    private val api: LocationApi = LocationApi(),
    private val locationDao: LocationDao
) {

    fun getLocalLocations(): Flow<List<LocationResponse>> {
        return locationDao.getAllLocations().map { entities ->
            entities.map { entity ->
                LocationResponse(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description ?: "Sarajevo location",
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    category = entity.category,
                    address = entity.address,
                    openingHours = entity.openingHours,
                    outdoorSeating = entity.outdoorSeating
                )
            }
        }
    }

    suspend fun syncLocationsFromApi() = withContext(Dispatchers.IO) {
        try {
            val apiLocations = api.getLocations()
            val entities = apiLocations.map { response ->
                LocationEntity(
                    id = response.id,
                    name = response.name,
                    description = response.description,
                    latitude = response.latitude,
                    longitude = response.longitude,
                    category = response.category,
                    address = response.address,
                    openingHours = response.openingHours,
                    outdoorSeating = response.outdoorSeating
                )
            }
            locationDao.clearLocations()
            locationDao.insertLocations(entities)
        } catch (e: Exception) {
            // Offline, keep Room data
        }
    }

    // 👈 FIXED: Read from Room first!
    suspend fun getLocationById(id: String): LocationResponse? {
        return try {
            // 1. Try to find in Room
            val localLocations = getLocalLocations().first()
            val localLocation = localLocations.firstOrNull { it.id == id }

            if (localLocation != null) {
                return localLocation
            }

            // 2. If not in Room, try API (fallback)
            api.getLocations().firstOrNull { it.id == id }
        } catch (e: Exception) {
            null
        }
    }
}