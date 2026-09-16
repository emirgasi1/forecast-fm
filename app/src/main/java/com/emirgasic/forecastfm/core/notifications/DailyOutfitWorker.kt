package com.emirgasic.forecastfm.core.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.emirgasic.forecastfm.network.location.LocationApi
import com.emirgasic.forecastfm.network.outfit.OutfitApi
import com.emirgasic.forecastfm.network.weather.WeatherApi

class DailyOutfitWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val context = applicationContext
            val prefs = NotificationPreferences(context)
            if (!prefs.isOutfitEnabled()) return Result.success()
            val locationApi = LocationApi()
            val weatherApi = WeatherApi()
            val outfitApi = OutfitApi()

            val locations = locationApi.getLocations()
            val location = locations.firstOrNull() ?: return Result.success()

            val weatherData = weatherApi.getWeather(
                location = location.name,
                latitude = location.latitude,
                longitude = location.longitude
            )

            val outfits = outfitApi.getOutfitsByWeather(weatherData.condition)
            val outfit = outfits.firstOrNull()

            val title = outfit?.title ?: "Check the Style tab for today's look"
            val body = outfit?.let {
                "${it.weatherCondition} today in ${location.name}. Try: ${it.title}"
            } ?: "No matching outfit for ${weatherData.condition}. Open Style for ideas."

            NotificationHelper.createChannels(context)
            NotificationHelper.post(
                context = context,
                channelId = NotificationHelper.CHANNEL_OUTFIT,
                notificationId = 3,
                title = title,
                body = body
            )

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "daily_outfit_worker"
    }
}