package com.emirgasic.forecastfm.core.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.emirgasic.forecastfm.R

object NotificationHelper {

    const val CHANNEL_WEATHER = "channel_weather"
    const val CHANNEL_PLAYLIST = "channel_playlist"
    const val CHANNEL_OUTFIT = "channel_outfit"
    const val CHANNEL_FRIEND = "channel_friend"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val weather = NotificationChannel(
            CHANNEL_WEATHER,
            "Weather Alerts",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications about significant weather changes"
        }

        val playlist = NotificationChannel(
            CHANNEL_PLAYLIST,
            "Daily Playlist",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Your morning playlist suggestion"
        }

        val outfit = NotificationChannel(
            CHANNEL_OUTFIT,
            "Outfit of the Day",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Weather-based outfit suggestion"
        }

        val friend = NotificationChannel(
            CHANNEL_FRIEND,
            "Activity",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Likes and comments on your posts"
        }

        manager.createNotificationChannels(
            listOf(weather, playlist, outfit, friend)
        )
    }

    fun canPost(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun post(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        body: String
    ) {
        if (!canPost(context)) return

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.mappin)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context)
                .notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}