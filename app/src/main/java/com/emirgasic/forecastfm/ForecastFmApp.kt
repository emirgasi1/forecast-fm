package com.emirgasic.forecastfm

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.emirgasic.forecastfm.core.notifications.DailyOutfitWorker
import com.emirgasic.forecastfm.core.notifications.DailyPlaylistWorker
import com.emirgasic.forecastfm.core.notifications.NotificationHelper
import com.emirgasic.forecastfm.core.notifications.WeatherAlertWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ForecastFmApp : Application() {

    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createChannels(this)

        scheduleWeatherWorker()

        scheduleDailyWorker(
            workName = DailyOutfitWorker.WORK_NAME,
            hour = 7,
            minute = 30
        ) { delayMs ->
            PeriodicWorkRequestBuilder<DailyOutfitWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        }

        scheduleDailyWorker(
            workName = DailyPlaylistWorker.WORK_NAME,
            hour = 8,
            minute = 0
        ) { delayMs ->
            PeriodicWorkRequestBuilder<DailyPlaylistWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        }
    }

    private fun scheduleWeatherWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<WeatherAlertWorker>(
            2, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WeatherAlertWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun scheduleDailyWorker(
        workName: String,
        hour: Int,
        minute: Int,
        buildRequest: (Long) -> PeriodicWorkRequest
    ) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)
        val delayMs = target.timeInMillis - now.timeInMillis

        val request = buildRequest(delayMs)

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            workName,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}