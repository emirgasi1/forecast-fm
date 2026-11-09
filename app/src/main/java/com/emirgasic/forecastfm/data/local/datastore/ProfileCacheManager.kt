package com.emirgasic.forecastfm.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.emirgasic.forecastfm.data.model.Profile
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

val Context.profileDataStore by preferencesDataStore(name = "profile_cache")

class ProfileCacheManager(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val PROFILE_JSON_KEY = stringPreferencesKey("profile_json")
    }

    suspend fun saveProfile(profile: Profile) {
        try {
            val jsonString = json.encodeToString(Profile.serializer(), profile)
            context.profileDataStore.edit { prefs ->
                prefs[PROFILE_JSON_KEY] = jsonString
            }
        } catch (e: Exception) {
            // Silent failure
        }
    }

    suspend fun getCachedProfile(): Profile? {
        return try {
            val prefs = context.profileDataStore.data.first()
            val jsonString = prefs[PROFILE_JSON_KEY] ?: return null
            json.decodeFromString(Profile.serializer(), jsonString)
        } catch (e: Exception) {
            null
        }
    }
}