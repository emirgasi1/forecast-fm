package com.emirgasic.forecastfm.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.Home
import com.emirgasic.forecastfm.data.repository.HomeRepository
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val homeRepository = HomeRepository()
    private val locationRepository = LocationRepository()
    private val weatherRepository = WeatherRepository()
    private val playlistRepository = PlaylistRepository(
        playlistApi = PlaylistApi()
    )

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                android.util.Log.d("HomeViewModel", "Loading home for user: $userId")

                val locations = locationRepository.getLocations()
                val location = locations.firstOrNull()
                    ?: throw Exception("No locations available")

                val weatherData = weatherRepository.getWeather(
                    location = location.name,
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                val allPlaylists = playlistRepository.getPlaylists()

                // Get trending playlists (first 3 sorted by likes or just first 3)
                val trendingPlaylists = allPlaylists
                    .sortedByDescending { it.likes }
                    .take(3)

                val home = Home(
                    greeting = homeRepository.getGreeting(),
                    weather = weatherData.weather,
                    forecast = weatherData.daily,
                    playlists = trendingPlaylists  // ← Only first 3 trending
                )

                _uiState.value = HomeUiState.Success(home)

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = HomeUiState.Error(
                    "Unable to load home data: ${e.message}"
                )
            }
        }
    }
}