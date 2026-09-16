package com.emirgasic.forecastfm.core.notifications

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.notificationStore by preferencesDataStore(name = "notification_prefs")

class NotificationPreferences(private val context: Context) {

    val weatherEnabled: Flow<Boolean> = context.notificationStore.data.map {
        it[WEATHER_KEY] ?: true
    }

    val playlistEnabled: Flow<Boolean> = context.notificationStore.data.map {
        it[PLAYLIST_KEY] ?: true
    }

    val outfitEnabled: Flow<Boolean> = context.notificationStore.data.map {
        it[OUTFIT_KEY] ?: true
    }

    val friendEnabled: Flow<Boolean> = context.notificationStore.data.map {
        it[FRIEND_KEY] ?: true
    }

    suspend fun setWeather(value: Boolean) {
        context.notificationStore.edit { it[WEATHER_KEY] = value }
    }

    suspend fun setPlaylist(value: Boolean) {
        context.notificationStore.edit { it[PLAYLIST_KEY] = value }
    }

    suspend fun setOutfit(value: Boolean) {
        context.notificationStore.edit { it[OUTFIT_KEY] = value }
    }

    suspend fun setFriend(value: Boolean) {
        context.notificationStore.edit { it[FRIEND_KEY] = value }
    }

    suspend fun isWeatherEnabled(): Boolean =
        context.notificationStore.data.first()[WEATHER_KEY] ?: true

    suspend fun isPlaylistEnabled(): Boolean =
        context.notificationStore.data.first()[PLAYLIST_KEY] ?: true

    suspend fun isOutfitEnabled(): Boolean =
        context.notificationStore.data.first()[OUTFIT_KEY] ?: true

    suspend fun isFriendEnabled(): Boolean =
        context.notificationStore.data.first()[FRIEND_KEY] ?: true

    companion object {
        private val WEATHER_KEY = booleanPreferencesKey("notif_weather")
        private val PLAYLIST_KEY = booleanPreferencesKey("notif_playlist")
        private val OUTFIT_KEY = booleanPreferencesKey("notif_outfit")
        private val FRIEND_KEY = booleanPreferencesKey("notif_friend")
    }
}