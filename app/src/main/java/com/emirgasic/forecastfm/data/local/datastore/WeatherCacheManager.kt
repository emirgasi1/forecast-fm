package com.emirgasic.forecastfm.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.emirgasic.forecastfm.data.model.WeatherData
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

val Context.weatherDataStore by preferencesDataStore(name = "weather_cache")

class WeatherCacheManager(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val WEATHER_JSON_KEY = stringPreferencesKey("weather_data_json")
        private val WEATHER_TIMESTAMP_KEY = longPreferencesKey("weather_timestamp")
        private const val CACHE_EXPIRY_MS = 30 * 60 * 1000L // 30 minutes
    }

    suspend fun saveWeatherData(data: WeatherData) {
        try {
            val jsonString = json.encodeToString(WeatherData.serializer(), data)
            context.weatherDataStore.edit { prefs ->
                prefs[WEATHER_JSON_KEY] = jsonString
                prefs[WEATHER_TIMESTAMP_KEY] = System.currentTimeMillis()
            }
        } catch (e: Exception) {
            // Silent failure
        }
    }

    suspend fun getCachedWeatherData(): WeatherData? {
        return try {
            val prefs = context.weatherDataStore.data.first()
            val jsonString = prefs[WEATHER_JSON_KEY] ?: return null
            val timestamp = prefs[WEATHER_TIMESTAMP_KEY] ?: 0L

            // Check if cache is expired
            val isExpired = (System.currentTimeMillis() - timestamp) > CACHE_EXPIRY_MS

            if (isExpired) {
                null // Force a network fetch
            } else {
                json.decodeFromString(WeatherData.serializer(), jsonString)
            }
        } catch (e: Exception) {
            null
        }
    }

    // Fallback for offline
    suspend fun getLastKnownWeatherData(): WeatherData? {
        return try {
            val prefs = context.weatherDataStore.data.first()
            val jsonString = prefs[WEATHER_JSON_KEY] ?: return null
            json.decodeFromString(WeatherData.serializer(), jsonString)
        } catch (e: Exception) {
            null
        }
    }
}