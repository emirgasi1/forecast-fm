package com.emirgasic.forecastfm.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.emirgasic.forecastfm.data.model.Outfit
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

val Context.outfitDataStore by preferencesDataStore(name = "outfit_cache")

class OutfitCacheManager(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val OUTFITS_JSON_KEY = stringPreferencesKey("outfits_json")
    }

    suspend fun saveOutfits(outfits: List<Outfit>) {
        try {
            val jsonString = json.encodeToString(kotlinx.serialization.builtins.ListSerializer(Outfit.serializer()), outfits)
            context.outfitDataStore.edit { prefs ->
                prefs[OUTFITS_JSON_KEY] = jsonString
            }
        } catch (e: Exception) {}
    }

    suspend fun getCachedOutfits(): List<Outfit>? {
        return try {
            val prefs = context.outfitDataStore.data.first()
            val jsonString = prefs[OUTFITS_JSON_KEY] ?: return null
            json.decodeFromString(kotlinx.serialization.builtins.ListSerializer(Outfit.serializer()), jsonString)
        } catch (e: Exception) {
            null
        }
    }
}