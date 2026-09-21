package com.emirgasic.forecastfm.core.onboarding

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.onboardingStore by preferencesDataStore(name = "onboarding_prefs")

class OnboardingPreferences(private val context: Context) {

    val onboardingCompleted: Flow<Boolean> = context.onboardingStore.data.map {
        it[COMPLETED_KEY] ?: false
    }

    val ageGroup: Flow<String?> = context.onboardingStore.data.map {
        it[AGE_KEY]
    }

    val companions: Flow<Set<String>> = context.onboardingStore.data.map {
        it[COMPANIONS_KEY] ?: emptySet()
    }

    val weatherPrefs: Flow<Set<String>> = context.onboardingStore.data.map {
        it[WEATHER_KEY] ?: emptySet()
    }

    val musicGenres: Flow<Set<String>> = context.onboardingStore.data.map {
        it[MUSIC_KEY] ?: emptySet()
    }

    val placeCategories: Flow<Set<String>> = context.onboardingStore.data.map {
        it[PLACES_KEY] ?: emptySet()
    }

    val moods: Flow<Set<String>> = context.onboardingStore.data.map {
        it[MOODS_KEY] ?: emptySet()
    }

    suspend fun setAgeGroup(value: String) {
        context.onboardingStore.edit { it[AGE_KEY] = value }
    }

    suspend fun setCompanions(values: Set<String>) {
        context.onboardingStore.edit { it[COMPANIONS_KEY] = values }
    }

    suspend fun setWeatherPrefs(values: Set<String>) {
        context.onboardingStore.edit { it[WEATHER_KEY] = values }
    }

    suspend fun setMusicGenres(values: Set<String>) {
        context.onboardingStore.edit { it[MUSIC_KEY] = values }
    }

    suspend fun setPlaceCategories(values: Set<String>) {
        context.onboardingStore.edit { it[PLACES_KEY] = values }
    }

    suspend fun setMoods(values: Set<String>) {
        context.onboardingStore.edit { it[MOODS_KEY] = values }
    }

    suspend fun markCompleted() {
        context.onboardingStore.edit { it[COMPLETED_KEY] = true }
    }

    suspend fun isCompleted(): Boolean =
        context.onboardingStore.data.first()[COMPLETED_KEY] ?: false

    suspend fun clear() {
        context.onboardingStore.edit { it.clear() }
    }

    companion object {
        private val COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        private val AGE_KEY = stringPreferencesKey("age_group")
        private val COMPANIONS_KEY = stringSetPreferencesKey("companions")
        private val WEATHER_KEY = stringSetPreferencesKey("weather_prefs")
        private val MUSIC_KEY = stringSetPreferencesKey("music_genres")
        private val PLACES_KEY = stringSetPreferencesKey("place_categories")
        private val MOODS_KEY = stringSetPreferencesKey("moods")
    }
}