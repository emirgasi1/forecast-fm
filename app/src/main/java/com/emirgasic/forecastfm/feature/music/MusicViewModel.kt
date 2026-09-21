package com.emirgasic.forecastfm.feature.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
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
class MusicViewModel(
    private val tokenManager: TokenManager,
    private val onboardingPrefs: OnboardingPreferences
) : ViewModel() {

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

    private val userGenres = MutableStateFlow<Set<String>>(emptySet())
    private val userMoods = MutableStateFlow<Set<String>>(emptySet())
    private val userWeather = MutableStateFlow<Set<String>>(emptySet())

    private val searchQuery = MutableStateFlow("")
    private val debounceTime = 500L

    init {
        loadUserPreferences()
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

    private fun loadUserPreferences() {
        viewModelScope.launch {
            userGenres.value = onboardingPrefs.musicGenres.first()
            userMoods.value = onboardingPrefs.moods.first()
            userWeather.value = onboardingPrefs.weatherPrefs.first()
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
        _weather,
        userGenres,
        userMoods,
        userWeather
    ) { playlists, weather, genres, moods, weatherPrefs ->
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
                .sortedWith(
                    compareByDescending<Playlist> {
                        scorePlaylist(it, genres, moods, weatherPrefs, weather.condition)
                    }.thenByDescending { it.likes }
                )
                .take(2)
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

        playlists
            .filter { it.id !in weatherIds }
            .sortedWith(
                compareByDescending<Playlist> {
                    scorePlaylist(it, genres, moods, weatherPrefs, currentCondition)
                }.thenByDescending { it.likes }
            )
            .take(4)
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
                scorePlaylist(it, genres, moods, weatherPrefs, currentCondition)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    private fun scorePlaylist(
        playlist: Playlist,
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

        searchJob?.cancel()

        if (value.isNotBlank()) {
            searchJob = viewModelScope.launch {
                delay(300)
                searchYouTube(value)
            }
        } else {
            _tracks.value = emptyList()
            _error.value = null
            searchJob?.cancel()
        }
    }

    fun selectGenre(value: String) {
        _selectedGenre.value = value
    }
}