package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.local.datastore.WeatherCacheManager
import com.emirgasic.forecastfm.data.model.Forecast
import com.emirgasic.forecastfm.data.model.Weather
import com.emirgasic.forecastfm.data.model.WeatherData // 👈 Import it from model now
import com.emirgasic.forecastfm.network.weather.WeatherApi

class WeatherRepository(
    private val weatherApi: WeatherApi = WeatherApi(),
    private val cacheManager: WeatherCacheManager? = null
) {

    suspend fun getWeather(
        location: String,
        latitude: Double,
        longitude: Double
    ): WeatherData {

        // 1. Try to get from DataStore
        if (cacheManager != null) {
            val cachedData = cacheManager.getCachedWeatherData()
            if (cachedData != null) {
                return cachedData
            }
        }

        // 2. Fetch from OpenWeather API
        return try {
            val response = weatherApi.getWeather(
                location = location,
                latitude = latitude,
                longitude = longitude
            )

            val weather = Weather(
                location = response.location,
                temperature = response.temperature,
                condition = response.condition,
                feelsLike = response.feelsLike,
                humidity = response.humidity,
                wind = response.wind,
                uvIndex = response.uvIndex,
                airQuality = response.airQuality,
                icon = conditionToIcon(response.condition)
            )

            val hourly = response.hourly.map { forecast ->
                Forecast(
                    time = forecast.time,
                    icon = conditionToIcon(forecast.condition),
                    temperature = forecast.temperature
                )
            }

            val daily = response.daily.map { forecast ->
                Forecast(
                    time = forecast.time,
                    icon = conditionToIcon(forecast.condition),
                    temperature = forecast.temperature
                )
            }

            val data = WeatherData(weather = weather, hourly = hourly, daily = daily)

            // 3. Save to DataStore
            if (cacheManager != null) {
                cacheManager.saveWeatherData(data)
            }

            data

        } catch (e: Exception) {
            // 4. Offline fallback
            if (cacheManager != null) {
                val fallback = cacheManager.getLastKnownWeatherData()
                if (fallback != null) {
                    return fallback
                }
            }
            throw Exception("No internet and no cached weather available")
        }
    }

    private fun conditionToIcon(condition: String): Int {
        return when (condition.lowercase()) {
            "sunny", "clear" -> R.drawable.sun
            "partly cloudy", "cloudy", "overcast" -> R.drawable.sunny_cloudy
            "rain", "drizzle", "light rain", "heavy rain" -> R.drawable.heavy_rain
            else -> R.drawable.sun
        }
    }
}