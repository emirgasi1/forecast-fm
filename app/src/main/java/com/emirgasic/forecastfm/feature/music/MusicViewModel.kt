package com.emirgasic.forecastfm.feature.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.MusicHistory
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.model.Weather
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.MusicHistoryRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import com.emirgasic.forecastfm.data.repository.YouTubeRepository
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import com.emirgasic.forecastfm.network.user.UserApi
import com.emirgasic.forecastfm.network.youtube.YouTubeVideo
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MusicViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val playlistRepository = PlaylistRepository(
        playlistApi = PlaylistApi()
    )
    private val locationRepository = LocationRepository()
    private val weatherRepository = WeatherRepository()
    private val musicHistoryRepository = MusicHistoryRepository()
    private val userRepository = UserRepository(
        userApi = UserApi()
    )
    private val youTubeRepository = YouTubeRepository()
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

    private val searchQuery = MutableStateFlow("")
    private val debounceTime = 500L
    private val _youtubeUrl = MutableStateFlow<String?>(null)
    val youtubeUrl: StateFlow<String?> = _youtubeUrl.asStateFlow()
    init {
        loadPlaylists()
        loadFavoritePlaylists()
        loadMusicHistory()
        viewModelScope.launch {
            searchQuery
                .debounce(debounceTime)
                .collect { query ->
                    if (query.isNotBlank()) {
                        searchYouTube(query)
                    } else {
                        _tracks.value = emptyList()
                        _error.value = null
                    }
                }
        }
    }



    private fun loadPlaylists() {
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

                val playlists = playlistRepository.getPlaylists()
                _playlists.value = playlists

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadFavoritePlaylists() {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    val favoriteIds = playlistRepository.getFavoritePlaylistIds(userId)
                    _favoritePlaylistIds.value = favoriteIds.toSet()
                }
            } catch (e: Exception) {
                println("FAVORITE PLAYLIST ERROR: ${e.message}")
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
        _weather
    ) { playlists, weather ->
        if (weather == null) {
            emptyList()
        } else {
            playlists
                .filter { playlist ->
                    when (weather.condition.lowercase()) {
                        "sunny", "clear" ->
                            playlist.weather.equals("Sunny", ignoreCase = true) ||
                                    playlist.weather.equals("Clear", ignoreCase = true)
                        "partly cloudy", "cloudy", "overcast" ->
                            playlist.weather.equals("Cloudy", ignoreCase = true) ||
                                    playlist.weather.equals("Partly cloudy", ignoreCase = true)
                        "rain", "drizzle", "light rain", "heavy rain" ->
                            playlist.weather.equals("Rain", ignoreCase = true) ||
                                    playlist.weather.equals("Rainy", ignoreCase = true)
                        else -> false
                    }
                }
                .take(2)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val trendingPlaylists = combine(
        filteredPlaylists,
        weatherPlaylists
    ) { playlists, weatherPlaylists ->
        val weatherIds = weatherPlaylists.map { it.id }.toSet()
        playlists
            .filter { it.id !in weatherIds }
            .sortedByDescending { it.likes }
            .take(4)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val recommendedPlaylist = combine(
        filteredPlaylists,
        _weather
    ) { playlists, weather ->
        if (playlists.isEmpty()) {
            null
        } else if (weather != null) {
            playlists
                .filter { it.weather.equals(weather.condition, ignoreCase = true) }
                .maxByOrNull { it.likes }
                ?: playlists.maxByOrNull { it.likes }
        } else {
            playlists.maxByOrNull { it.likes }
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
    fun clearYoutubeUrl() {
        _youtubeUrl.value = null
    }
    fun searchAndOpenYouTube(query: String) {
        // Use fallback directly since YouTube API quota is exceeded
        _youtubeUrl.value = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
    }
    /*
    fun searchAndOpenYouTube(query: String) {
        viewModelScope.launch {
            try {
                val results = youTubeRepository.searchVideos(query)
                val firstVideo = results.firstOrNull()
                if (firstVideo != null) {
                    val videoId = firstVideo.id?.videoId
                    if (videoId != null) {
                        _youtubeUrl.value = "https://www.youtube.com/watch?v=$videoId"
                    }
                }
            } catch (e: Exception) {
                _youtubeUrl.value = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
            }
        }
    } */

    fun loadMusicHistory() {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    val history = musicHistoryRepository.getMusicHistory(userId)
                    _musicHistory.value = history
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun toggleFavorite(playlistId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId == null) {
                    println("FAVORITE ERROR: User not logged in")
                    return@launch
                }

                val isFavorite = playlistId in _favoritePlaylistIds.value

                if (isFavorite) {
                    playlistRepository.unfavoritePlaylist(
                        userId = userId,
                        playlistId = playlistId
                    )
                    _favoritePlaylistIds.value = _favoritePlaylistIds.value - playlistId
                } else {
                    playlistRepository.favoritePlaylist(
                        userId = userId,
                        playlistId = playlistId
                    )
                    _favoritePlaylistIds.value = _favoritePlaylistIds.value + playlistId
                }
            } catch (e: Exception) {
                println("FAVORITE ERROR: ${e.message}")
            }
        }
    }

    fun openPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId == null) {
                    println("HISTORY ERROR: User not logged in")
                    return@launch
                }

                musicHistoryRepository.addHistory(
                    userId = userId,
                    playlistId = playlist.id
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private var searchJob: Job? = null

    fun updateSearch(value: String) {
        _search.value = value

        // Cancel previous search
        searchJob?.cancel()

        if (value.isNotBlank()) {
            // Add small delay before searching
            searchJob = viewModelScope.launch {
                delay(300) // Wait 300ms before searching
                searchYouTube(value)
            }
        } else {
            _tracks.value = emptyList()
            _error.value = null
            searchJob?.cancel()
        }
    }

    private fun getWeatherPlaylist(condition: String): String {
        return when (condition.lowercase()) {
            "sunny", "clear" -> "Sunny Vibes"
            "rain", "drizzle", "light rain", "heavy rain" -> "Rainy Day"
            "cloudy", "overcast", "partly cloudy" -> "Cloudy Chill"
            "snow", "sleet" -> "Winter Wonderland"
            else -> "Mixed Weather"
        }
    }


    fun selectGenre(value: String) {
        _selectedGenre.value = value
    }
}