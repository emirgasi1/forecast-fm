package com.emirgasic.forecastfm.feature.settings.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.notifications.NotificationPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val prefs: NotificationPreferences
) : ViewModel() {

    private val _weather = MutableStateFlow(true)
    val weather: StateFlow<Boolean> = _weather.asStateFlow()

    private val _playlist = MutableStateFlow(true)
    val playlist: StateFlow<Boolean> = _playlist.asStateFlow()

    private val _outfit = MutableStateFlow(true)
    val outfit: StateFlow<Boolean> = _outfit.asStateFlow()

    private val _friend = MutableStateFlow(true)
    val friend: StateFlow<Boolean> = _friend.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.weatherEnabled.collect { _weather.value = it }
        }
        viewModelScope.launch {
            prefs.playlistEnabled.collect { _playlist.value = it }
        }
        viewModelScope.launch {
            prefs.outfitEnabled.collect { _outfit.value = it }
        }
        viewModelScope.launch {
            prefs.friendEnabled.collect { _friend.value = it }
        }
    }

    fun setWeather(value: Boolean) {
        viewModelScope.launch { prefs.setWeather(value) }
    }

    fun setPlaylist(value: Boolean) {
        viewModelScope.launch { prefs.setPlaylist(value) }
    }

    fun setOutfit(value: Boolean) {
        viewModelScope.launch { prefs.setOutfit(value) }
    }

    fun setFriend(value: Boolean) {
        viewModelScope.launch { prefs.setFriend(value) }
    }
}