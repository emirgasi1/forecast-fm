package com.emirgasic.forecastfm.network.place

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PlaceApi {

    suspend fun getPlacesByVenue(venueId: String): List<PlaceResponse> {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/places/venue/$venueId").body()
    }

    suspend fun getPlaceById(id: String): PlaceResponse {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/places/$id").body()
    }
}