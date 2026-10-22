package com.emirgasic.forecastfm.network.place

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.encodeURLPathPart

class PlaceApi {

    // CHANGED: Pointing to /api/venues instead of /api/places
    suspend fun getAllPlaces(): List<PlaceResponse> {
        return ApiClient.client
            .get("${ApiClient.baseUrl()}/api/venues")
            .body()
    }

    // (Leave the rest as-is, they handle specific places)
    suspend fun getPlacesByVenue(venueId: String): List<PlaceResponse> {
        return ApiClient.client
            .get("${ApiClient.baseUrl()}/api/places/venue/${venueId.encodeURLPathPart()}")
            .body()
    }

    suspend fun getPlaceById(id: String): PlaceResponse {
        return ApiClient.client
            .get("${ApiClient.baseUrl()}/api/places") {
                parameter("id", id)
            }
            .body()
    }

    suspend fun searchPlaces(query: String): List<PlaceResponse> {
        return ApiClient.client
            .get("${ApiClient.baseUrl()}/api/places/search") {
                parameter("q", query)
            }
            .body()
    }

    suspend fun getPlacesCountByVenue(venueId: String): Int {
        return ApiClient.client
            .get("${ApiClient.baseUrl()}/api/places/count") {
                parameter("venueId", venueId)
            }
            .body<PlaceCountResponse>()
            .count
    }
}