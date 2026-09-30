package com.emirgasic.forecastfm.feature.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import com.emirgasic.forecastfm.data.model.MusicHistory
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.model.Weather
import com.emirgasic.forecastfm.data.recommender.RecommendationScorer
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.MusicHistoryRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import com.emirgasic.forecastfm.data.repository.YouTubeRepository
import com.emirgasic.forecastfm.network.youtube.YouTubeVideo
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MusicViewModel(
    private val tokenManager: TokenManager,
    private val onboardingPrefs: OnboardingPreferences,
    private val playlistRepository: PlaylistRepository,
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val musicHistoryRepository: MusicHistoryRepository,
    private val userRepository: UserRepository,
    private val youTubeRepository: YouTubeRepository
) : ViewModel() {

    private val _musicHistory = MutableStateFlow<List<MusicHistory>>(emptyList())
    val musicHistory: StateFlow<List<MusicHistory>> = _musicHistory.asStateFlow()

    private val _weather = MutableStateFlow<Weather?>(null)
    val weather = _weather.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _playlists.asStateFlow()

    private val _favoritePlaylistIds = MutableStateFlow<Set<String>>(emptySet())
    val favoritePlaylistIds = _favoritePlaylistIds.asStateFlow()

    private val _search = MutableStateFlow("")
    val search = _search.asStateFlow()

    private val _selectedGenre = MutableStateFlow("")
    val selectedGenre = _selectedGenre.asStateFlow()

    private val _tracks = MutableStateFlow<List<YouTubeVideo>>(emptyList())
    val tracks = _tracks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val userGenres = MutableStateFlow<Set<String>>(emptySet())
    private val userMoods = MutableStateFlow<Set<String>>(emptySet())
    private val userWeather = MutableStateFlow<Set<String>>(emptySet())

    fun loadUserPreferences() {
        viewModelScope.launch {
            userGenres.value = onboardingPrefs.musicGenres.first()
            userMoods.value = onboardingPrefs.moods.first()
            userWeather.value = onboardingPrefs.weatherPrefs.first()
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            try {
                val locations = locationRepository.getLocations()
                val location = locations.firstOrNull()
                    ?: throw Exception("No locations available")

                val weatherData = weatherRepository.getWeather(
                    location = location.name,
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                _weather.value = weatherData.weather
                _playlists.value = playlistRepository.getPlaylists()
            } catch (_: Exception) {
            }
        }
    }

    fun loadFavoritePlaylists() {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    val favoriteIds = playlistRepository.getFavoritePlaylistIds(userId)
                    _favoritePlaylistIds.value = favoriteIds.toSet()
                }
            } catch (_: Exception) {
            }
        }
    }

    val filteredPlaylists = combine(
        _playlists,
        _search,
        _selectedGenre
    ) { playlists, search, genre ->
        playlists.filter { playlist ->
            val matchesSearch = search.isBlank() ||
                    playlist.title.contains(search, ignoreCase = true) ||
                    playlist.genre.contains(search, ignoreCase = true) ||
                    playlist.mood.contains(search, ignoreCase = true)

            val matchesGenre = genre.isBlank() ||
                    playlist.genre.equals(genre, ignoreCase = true)

            matchesSearch && matchesGenre
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val weatherPlaylists = combine(
        filteredPlaylists,
        _weather,
        userGenres,
        userMoods,
        userWeather
    ) { playlists, weather, genres, moods, weatherPrefs ->
        if (weather == null) {
            emptyList()
        } else {
            val matching = playlists.filter { playlist ->
                RecommendationScorer.weatherCategoryMatches(
                    currentCondition = weather.condition,
                    playlistWeather = playlist.weather
                )
            }
            RecommendationScorer.rankPlaylists(
                allPlaylists = matching,
                userGenres = genres,
                userMoods = moods,
                userWeather = weatherPrefs,
                currentWeather = weather.condition,
                take = 2
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val trendingPlaylists = combine(
        filteredPlaylists,
        weatherPlaylists,
        userGenres,
        userMoods,
        userWeather,
        _weather
    ) { values ->
        val playlists = values[0] as List<Playlist>
        val weatherPlaylists = values[1] as List<Playlist>
        val genres = values[2] as Set<String>
        val moods = values[3] as Set<String>
        val weatherPrefs = values[4] as Set<String>
        val weather = values[5] as Weather?

        val weatherIds = weatherPlaylists.map { it.id }.toSet()
        val currentCondition = weather?.condition ?: ""

        val candidates = playlists.filter { it.id !in weatherIds }

        RecommendationScorer.rankPlaylists(
            allPlaylists = candidates,
            userGenres = genres,
            userMoods = moods,
            userWeather = weatherPrefs,
            currentWeather = currentCondition,
            take = 4
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val recommendedPlaylist = combine(
        filteredPlaylists,
        _weather,
        userGenres,
        userMoods,
        userWeather
    ) { playlists, weather, genres, moods, weatherPrefs ->
        if (playlists.isEmpty()) {
            null
        } else {
            val currentCondition = weather?.condition ?: ""
            playlists.maxByOrNull {
                RecommendationScorer.scorePlaylist(
                    playlist = it,
                    userGenres = genres,
                    userMoods = moods,
                    userWeather = weatherPrefs,
                    currentWeather = currentCondition
                )
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    fun searchYouTube(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val results = youTubeRepository.searchVideos(query)
                _tracks.value = results
                if (results.isEmpty()) {
                    _error.value = "No results found"
                }
            } catch (e: Exception) {
                if (e.message?.contains("429") == true) {
                    _error.value = "API quota exceeded. Please try again tomorrow."
                } else {
                    _error.value = e.message
                }
            }
            _isLoading.value = false
        }
    }

    fun loadMusicHistory() {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    _musicHistory.value = musicHistoryRepository.getMusicHistory(userId)
                }
            } catch (_: Exception) {
            }
        }
    }

    fun toggleFavorite(playlistId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first() ?: return@launch

                val isFavorite = playlistId in _favoritePlaylistIds.value

                if (isFavorite) {
                    playlistRepository.unfavoritePlaylist(userId, playlistId)
                    _favoritePlaylistIds.value = _favoritePlaylistIds.value - playlistId
                } else {
                    playlistRepository.favoritePlaylist(userId, playlistId)
                    _favoritePlaylistIds.value = _favoritePlaylistIds.value + playlistId
                }
            } catch (_: Exception) {
            }
        }
    }

    fun openPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first() ?: return@launch
                musicHistoryRepository.addHistory(userId, playlist.id)
            } catch (_: Exception) {
            }
        }
    }

    private var searchJob: Job? = null

    fun updateSearch(value: String) {
        _search.value = value

        searchJob?.cancel()

        if (value.isNotBlank()) {
            searchJob = viewModelScope.launch {
                delay(300)
                searchYouTube(value)
            }
        } else {
            _tracks.value = emptyList()
            _error.value = null
        }
    }

    fun selectGenre(value: String) {
        _selectedGenre.value = value
    }
}