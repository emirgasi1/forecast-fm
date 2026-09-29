package com.emirgasic.forecastfm.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import com.emirgasic.forecastfm.data.model.Home
import com.emirgasic.forecastfm.data.recommender.HomeRecommender
import com.emirgasic.forecastfm.data.repository.HomeRepository
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val tokenManager: TokenManager,
    private val onboardingPrefs: OnboardingPreferences,
    private val homeRepository: HomeRepository,
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadHome(userLatitude: Double?, userLongitude: Double?) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")


                @Suppress("UNUSED_EXPRESSION")
                userId

                val locations = locationRepository.getLocations()
                if (locations.isEmpty()) throw Exception("No locations available")

                val location = HomeRecommender.pickNearestLocation(
                    locations = locations,
                    userLat = userLatitude,
                    userLng = userLongitude
                )

                val weatherData = weatherRepository.getWeather(
                    location = location.name,
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                val allPlaylists = playlistRepository.getPlaylists()

                val userGenres = onboardingPrefs.musicGenres.first()
                val userMoods = onboardingPrefs.moods.first()
                val userWeather = onboardingPrefs.weatherPrefs.first()

                val finalPlaylists = HomeRecommender.rankPlaylists(
                    allPlaylists = allPlaylists,
                    userGenres = userGenres,
                    userMoods = userMoods,
                    userWeather = userWeather,
                    currentWeather = weatherData.weather.condition
                )

                val home = Home(
                    greeting = homeRepository.getGreeting(),
                    weather = weatherData.weather,
                    forecast = weatherData.daily,
                    playlists = finalPlaylists
                )

                _uiState.value = HomeUiState.Success(home)

            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    "Unable to load home data: ${e.message}"
                )
            }
        }
    }
}