package com.emirgasic.forecastfm.network.weather

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType
import kotlinx.serialization.json.Json


class WeatherApi {

    suspend fun getWeather(
        location: String,
        latitude: Double,
        longitude: Double
    ): WeatherResponse {

        return ApiClient.client
            .get("${ApiClient.baseUrl()}/api/weather") {

                parameter("location", location)
                parameter("latitude", latitude)
                parameter("longitude", longitude)

            }
            .body()
    }
}