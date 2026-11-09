package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.local.daos.place.PlaceDao
import com.emirgasic.forecastfm.data.local.entities.place.PlaceEntity
import com.emirgasic.forecastfm.data.local.entities.saved.SavedPlaceEntity
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.place.PlaceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaceRepository(
    private val placeDao: PlaceDao,
    private val placeApi: PlaceApi = PlaceApi()
) {

    fun getLocalPlaces(): Flow<List<Place>> {
        return placeDao.getAllPlaces().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun syncPlacesFromApi() {
        try {
            val responses = placeApi.getAllPlaces()
            val entities = responses.map { response ->
                PlaceEntity(
                    id = response.id,
                    name = response.name,
                    category = response.category,
                    venueId = response.venueId,
                    address = response.address,
                    latitude = response.latitude,
                    longitude = response.longitude,
                    description = response.description,
                    imageUrl = response.imageUrl,
                    openingHours = response.openingHours,
                    outdoorSeating = response.outdoorSeating
                )
            }
            placeDao.clearPlaces()
            placeDao.insertPlaces(entities)
        } catch (e: Exception) {
            // Handle offline (do nothing, Room already has the old data)
        }
    }

    fun getSavedPlaces(): Flow<List<String>> {
        return placeDao.getSavedPlaces().map { list -> list.map { it.placeId } }
    }

    fun isPlaceSaved(placeId: String): Flow<Boolean> {
        return placeDao.isPlaceSaved(placeId)
    }

    suspend fun toggleSavePlace(placeId: String, isCurrentlySaved: Boolean) {
        if (isCurrentlySaved) {
            placeDao.unsavePlace(placeId)
        } else {
            placeDao.savePlace(SavedPlaceEntity(placeId))
        }
    }


    suspend fun getAllPlaces(): List<Place> {
        val responses = placeApi.getAllPlaces()
        return responses.map { it.toDomain() }
    }

    suspend fun getPlacesByVenue(venueId: String): List<Place> {

        val responses = placeApi.getVenuesByLocation(venueId)
        return responses.map { it.toDomain() }
    }

    suspend fun getPlaceById(id: String): Place? {
        return try {
            val response = placeApi.getPlaceById(id)
            response.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchPlaces(query: String): List<Place> {
        val responses = placeApi.searchPlaces(query)
        return responses.map { it.toDomain() }
    }

    suspend fun getPlacesCountByVenue(venueId: String): Int {
        return try {
            placeApi.getPlacesCountByVenue(venueId)
        } catch (e: Exception) {
            0
        }
    }

    private fun PlaceEntity.toDomain() = Place(
        id = id,
        name = name,
        category = category,
        venueId = venueId,
        address = address ?: "",
        latitude = latitude,
        longitude = longitude,
        description = description ?: "",
        imageUrl = imageUrl,
        openingHours = openingHours,
        outdoorSeating = outdoorSeating
    )

    private fun com.emirgasic.forecastfm.network.place.PlaceResponse.toDomain() = Place(
        id = id,
        name = name,
        category = category,
        venueId = venueId,
        address = address ?: "",
        latitude = latitude,
        longitude = longitude,
        description = description ?: "",
        imageUrl = if (imageUrl != null) "${ApiClient.baseUrl()}${imageUrl}" else null,
        openingHours = openingHours,
        outdoorSeating = outdoorSeating
    )
}