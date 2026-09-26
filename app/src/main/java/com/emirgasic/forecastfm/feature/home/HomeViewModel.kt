package com.emirgasic.forecastfm.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HomeViewModel(
    private val tokenManager: TokenManager,
    private val onboardingPrefs: OnboardingPreferences
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
        loadHome(null, null)
    }

    fun loadHome(userLatitude: Double?, userLongitude: Double?) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                val locations = locationRepository.getLocations()
                if (locations.isEmpty()) throw Exception("No locations available")

                val location = pickNearestLocation(
                    locations = locations,
                    userLat = userLatitude,
                    userLng = userLongitude
                )

                Log.d("HomeVM", "Using location: ${location.name} (${location.latitude}, ${location.longitude})")

                val weatherData = weatherRepository.getWeather(
                    location = location.name,
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                val allPlaylists = playlistRepository.getPlaylists()

                val userGenres = onboardingPrefs.musicGenres.first()
                val userMoods = onboardingPrefs.moods.first()
                val userWeather = onboardingPrefs.weatherPrefs.first()

                val scoredPlaylists = allPlaylists
                    .map { playlist ->
                        val score = scorePlaylist(
                            playlist = playlist,
                            userGenres = userGenres,
                            userMoods = userMoods,
                            userWeather = userWeather,
                            currentWeather = weatherData.weather.condition
                        )
                        playlist to score
                    }
                    .sortedWith(
                        compareByDescending<Pair<com.emirgasic.forecastfm.data.model.Playlist, Int>> { it.second }
                            .thenByDescending { it.first.likes }
                    )
                    .take(3)
                    .map { it.first }

                val finalPlaylists = if (scoredPlaylists.isEmpty()) {
                    allPlaylists.sortedByDescending { it.likes }.take(3)
                } else {
                    scoredPlaylists
                }

                val home = Home(
                    greeting = homeRepository.getGreeting(),
                    weather = weatherData.weather,
                    forecast = weatherData.daily,
                    playlists = finalPlaylists
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

    private fun pickNearestLocation(
        locations: List<com.emirgasic.forecastfm.network.location.LocationResponse>,
        userLat: Double?,
        userLng: Double?
    ): com.emirgasic.forecastfm.network.location.LocationResponse {
        if (userLat == null || userLng == null) {
            return locations.first()
        }

        return locations.minByOrNull { loc ->
            haversineKm(userLat, userLng, loc.latitude, loc.longitude)
        } ?: locations.first()
    }

    private fun haversineKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).let { it * it } +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).let { it * it }
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun scorePlaylist(
        playlist: com.emirgasic.forecastfm.data.model.Playlist,
        userGenres: Set<String>,
        userMoods: Set<String>,
        userWeather: Set<String>,
        currentWeather: String
    ): Int {
        var score = 0

        if (userGenres.isNotEmpty() && userGenres.any { genreMatches(it, playlist.genre) }) {
            score += 10
        }

        if (userMoods.isNotEmpty() && userMoods.any { moodMatches(it, playlist.mood) }) {
            score += 6
        }

        if (userWeather.isNotEmpty() && userWeather.any { weatherMatches(it, currentWeather) }) {
            score += 8
        } else if (playlist.weather.equals(currentWeather, ignoreCase = true)) {
            score += 4
        }

        score += (playlist.likes / 20)

        return score
    }

    private fun genreMatches(userPick: String, dbGenre: String): Boolean {
        val pick = userPick.lowercase().trim()
        val genre = dbGenre.lowercase().trim()

        if (genre.contains(pick) || pick.contains(genre)) return true

        return when (pick) {
            "indie / alternative" -> genre.contains("indie")
            "r&b / soul" -> genre.contains("r&b") || genre.contains("soul")
            "sevdah / traditional" -> genre.contains("sevdah") || genre.contains("traditional")
            "hip-hop" -> genre.contains("hip-hop") || genre.contains("hip hop")
            "electronic" -> genre.contains("electronic") || genre.contains("edm")
            "jazz / blues" -> genre.contains("jazz") || genre.contains("blues")
            else -> false
        }
    }

    private fun moodMatches(userPick: String, dbMood: String): Boolean {
        val pick = userPick.lowercase().trim()
        val mood = dbMood.lowercase().trim()

        if (mood.contains(pick) || pick.contains(mood)) return true

        return when (pick) {
            "chill & cozy" -> mood.contains("chill") || mood.contains("cozy")
            "energetic & social" -> mood.contains("energetic") || mood.contains("party") || mood.contains("social")
            "romantic" -> mood.contains("romantic") || mood.contains("date")
            "focused & productive" -> mood.contains("focus") || mood.contains("work")
            "adventurous" -> mood.contains("adventure") || mood.contains("outdoor")
            "feels good / sunny" -> mood.contains("feel good") || mood.contains("sunny")
            else -> false
        }
    }

    private fun weatherMatches(userPick: String, currentWeather: String): Boolean {
        val pick = userPick.lowercase().trim()
        val weather = currentWeather.lowercase().trim()

        if (weather.contains(pick) || pick.contains(weather)) return true

        return when (pick) {
            "sunny & clear" -> weather.contains("sunny") || weather.contains("clear")
            "partly cloudy" -> weather.contains("cloud") || weather.contains("partly")
            "rainy" -> weather.contains("rain") || weather.contains("drizzle")
            "snowy" -> weather.contains("snow")
            "cool & crisp" -> weather.contains("cold") || weather.contains("cool")
            "hot days" -> weather.contains("hot")
            "mild weather" -> weather.contains("mild") || weather.contains("clear")
            else -> false
        }
    }
}