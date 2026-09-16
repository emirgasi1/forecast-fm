package com.emirgasic.forecastfm.feature.settings.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emirgasic.forecastfm.core.notifications.NotificationPreferences
import com.emirgasic.forecastfm.core.ui.components.settings.NotificationToggleRow

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: NotificationsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return NotificationsViewModel(NotificationPreferences(context)) as T
            }
        }
    )

    val weather by viewModel.weather.collectAsState()
    val playlist by viewModel.playlist.collectAsState()
    val outfit by viewModel.outfit.collectAsState()
    val friend by viewModel.friend.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 60.dp,
                start = 20.dp,
                end = 20.dp,
                bottom = 20.dp
            )
    ) {
        Text(
            text = "Notifications",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        NotificationToggleRow(
            title = "Weather alerts",
            subtitle = "Notify when weather changes significantly",
            checked = weather,
            onCheckedChange = { viewModel.setWeather(it) }
        )

        NotificationToggleRow(
            title = "Daily playlist",
            subtitle = "Morning playlist suggestion at 8:00 AM",
            checked = playlist,
            onCheckedChange = { viewModel.setPlaylist(it) }
        )

        NotificationToggleRow(
            title = "Outfit of the day",
            subtitle = "Weather-based outfit at 7:30 AM",
            checked = outfit,
            onCheckedChange = { viewModel.setOutfit(it) }
        )

        NotificationToggleRow(
            title = "Activity",
            subtitle = "Likes and comments on your posts",
            checked = friend,
            onCheckedChange = { viewModel.setFriend(it) }
        )
    }
}