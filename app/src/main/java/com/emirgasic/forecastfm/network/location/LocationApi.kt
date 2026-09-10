package com.emirgasic.forecastfm.network.location

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class LocationApi {

    private val client = ApiClient.client

    suspend fun getLocations(): List<LocationResponse> {
        return client
            .get("${ApiClient.baseUrl()}/api/locations")
            .body()
    }
}