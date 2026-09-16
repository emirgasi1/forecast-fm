package com.emirgasic.forecastfm.core.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.emirgasic.forecastfm.network.location.LocationApi
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import com.emirgasic.forecastfm.network.weather.WeatherApi

class DailyPlaylistWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val context = applicationContext
            val prefs = NotificationPreferences(context)
            if (!prefs.isPlaylistEnabled()) return Result.success()
            val locationApi = LocationApi()
            val weatherApi = WeatherApi()
            val playlistApi = PlaylistApi()

            val locations = locationApi.getLocations()
            val location = locations.firstOrNull() ?: return Result.success()

            val weatherData = weatherApi.getWeather(
                location = location.name,
                latitude = location.latitude,
                longitude = location.longitude
            )

            val playlists = playlistApi.getPlaylists()
            val matching = playlists.firstOrNull {
                it.weather.equals(weatherData.condition, ignoreCase = true)
            } ?: playlists.firstOrNull()

            val playlistName = matching?.title ?: "Sarajevo Morning"

            NotificationHelper.createChannels(context)
            NotificationHelper.post(
                context = context,
                channelId = NotificationHelper.CHANNEL_PLAYLIST,
                notificationId = 2,
                title = "Good morning, Sarajevo",
                body = "Today's soundtrack for ${location.name}: $playlistName"
            )

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "daily_playlist_worker"
    }
}