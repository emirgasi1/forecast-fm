package com.emirgasic.forecastfm.network.place

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.encodeURLPathPart

class PlaceApi {

    suspend fun getAllPlaces(): List<PlaceResponse> {
        return ApiClient.client
            .get("${ApiClient.baseUrl()}/api/venues")
            .body()
    }

    suspend fun getVenuesByLocation(locationId: String): List<PlaceResponse> {
        return ApiClient.client
            .get("${ApiClient.baseUrl()}/api/venues/location/${locationId.encodeURLPathPart()}")
            .body()
    }

    suspend fun getPlaceById(id: String): PlaceResponse {
        return ApiClient.client
            .get("${ApiClient.baseUrl()}/api/venues/${id.encodeURLPathPart()}")
            .body()
    }

    suspend fun searchPlaces(query: String): List<PlaceResponse> {
        return ApiClient.client
            .get("${ApiClient.baseUrl()}/api/venues/search") {
                parameter("q", query)
            }
            .body()
    }

  suspend fun getPlacesCountByVenue(venueId: String): Int {
        return try {
            ApiClient.client
                .get("${ApiClient.baseUrl()}/api/places/count") {
                    parameter("venueId", venueId)
                }
                .body<PlaceCountResponse>()
                .count
        } catch (e: Exception) {
            0
        }
    }
}