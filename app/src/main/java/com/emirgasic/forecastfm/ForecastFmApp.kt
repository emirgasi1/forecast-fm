package com.emirgasic.forecastfm

import android.app.Application
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.emirgasic.forecastfm.core.notifications.DailyOutfitWorker
import com.emirgasic.forecastfm.core.notifications.DailyPlaylistWorker
import com.emirgasic.forecastfm.core.notifications.NotificationHelper
import com.emirgasic.forecastfm.core.notifications.WeatherAlertWorker
import com.emirgasic.forecastfm.data.local.AppDatabase
import com.emirgasic.forecastfm.di.ServiceLocator
import com.emirgasic.forecastfm.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ForecastFmApp : Application(), SingletonImageLoader.Factory {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "forecastfm_db"
        ).build()
    }
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
        appScope.launch {
            runCatching { ApiClient.client }
        }

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

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory())
            }
            .crossfade(true)
            .build()
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