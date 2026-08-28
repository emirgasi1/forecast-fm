package com.emirgasic.forecastfm.network.weather

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType
import kotlinx.serialization.json.Json


class WeatherApi {

    suspend fun getWeather(): WeatherResponse {

        val response = ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/weather"
        )

        val body = response.bodyAsText()

        return Json.decodeFromString<WeatherResponse>(body)
    }
}