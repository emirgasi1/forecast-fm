package com.emirgasic.forecastfm.core.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.emirgasic.forecastfm.network.location.LocationApi
import com.emirgasic.forecastfm.network.weather.WeatherApi
import kotlinx.coroutines.flow.first

private val Context.weatherStore by preferencesDataStore(name = "weather_alert")

class WeatherAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val context = applicationContext
            val prefs = NotificationPreferences(context)
            if (!prefs.isWeatherEnabled()) return Result.success()
            val locationApi = LocationApi()
            val weatherApi = WeatherApi()

            val locations = locationApi.getLocations()
            val location = locations.firstOrNull() ?: return Result.success()

            val weatherData = weatherApi.getWeather(
                location = location.name,
                latitude = location.latitude,
                longitude = location.longitude
            )

            val currentCondition = weatherData.condition
            val currentTemp = weatherData.temperature

            val lastCondition = readLastCondition(context)
            val lastTemp = readLastTemp(context)

            if (lastCondition != null && lastCondition != currentCondition) {
                NotificationHelper.createChannels(context)
                NotificationHelper.post(
                    context = context,
                    channelId = NotificationHelper.CHANNEL_WEATHER,
                    notificationId = 1,
                    title = "Weather update for ${location.name}",
                    body = "Conditions changed from $lastCondition to $currentCondition. Now $currentTemp."
                )
            } else if (lastTemp != null && hasSignificantTempDrop(lastTemp, currentTemp)) {
                NotificationHelper.createChannels(context)
                NotificationHelper.post(
                    context = context,
                    channelId = NotificationHelper.CHANNEL_WEATHER,
                    notificationId = 1,
                    title = "Temperature drop in ${location.name}",
                    body = "It was $lastTemp, now $currentTemp. Dress accordingly."
                )
            }

            saveWeather(context, currentCondition, currentTemp)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun hasSignificantTempDrop(last: String, current: String): Boolean {
        val lastNum = last.filter { it.isDigit() || it == '-' }.toIntOrNull() ?: return false
        val currentNum = current.filter { it.isDigit() || it == '-' }.toIntOrNull() ?: return false
        return lastNum - currentNum >= 5
    }

    private suspend fun readLastCondition(context: Context): String? {
        return context.weatherStore.data.first()[CONDITION_KEY]
    }

    private suspend fun readLastTemp(context: Context): String? {
        return context.weatherStore.data.first()[TEMP_KEY]
    }

    private suspend fun saveWeather(context: Context, condition: String, temp: String) {
        context.weatherStore.edit {
            it[CONDITION_KEY] = condition
            it[TEMP_KEY] = temp
        }
    }

    companion object {
        private val CONDITION_KEY = stringPreferencesKey("last_condition")
        private val TEMP_KEY = stringPreferencesKey("last_temp")
        const val WORK_NAME = "weather_alert_worker"
    }
}