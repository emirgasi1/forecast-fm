
package com.emirgasic.forecastfm.feature.music

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.Weather
import com.emirgasic.forecastfm.data.repository.MusicHistoryRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import com.emirgasic.forecastfm.network.playlist.PlaylistResponse
import com.emirgasic.forecastfm.network.user.UserApi
import com.emirgasic.forecastfm.repository.WeatherRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusicViewModel : ViewModel() {

    private val playlistRepository = PlaylistRepository(
        playlistApi = PlaylistApi()
    )
    private val weatherRepository = WeatherRepository()
    private val _weather = MutableStateFlow<Weather?>(null)
    private val musicHistoryRepository = MusicHistoryRepository()
    val weather = _weather.asStateFlow()
    private val userRepository = UserRepository(
        userApi = UserApi()
    )

    private val _playlists =
        MutableStateFlow<List<Playlist>>(emptyList())

    val playlists =
        _playlists.asStateFlow()

    private val _favoritePlaylistIds =
        MutableStateFlow<Set<String>>(emptySet())

    val favoritePlaylistIds =
        _favoritePlaylistIds.asStateFlow()

    private val _search =
        MutableStateFlow("")

    val search =
        _search.asStateFlow()

    private val _selectedGenre =
        MutableStateFlow("")

    val selectedGenre =
        _selectedGenre.asStateFlow()



    init {
        loadPlaylists()
        loadFavoritePlaylists()
    }

    private fun loadPlaylists() {

        viewModelScope.launch {

            try {

                println("WEATHER START")

                val weatherData =
                    weatherRepository.getWeather()

                println("WEATHER SUCCESS")

                _weather.value =
                    weatherData.weather

                println("PLAYLIST START")

                val playlists =
                    playlistRepository.getPlaylists()

                println("PLAYLIST SUCCESS: ${playlists.size}")

                _playlists.value =
                    playlists

            } catch (e: Exception) {

                e.printStackTrace()

                println(
                    "MUSIC VIEWMODEL ERROR: ${e.message}"
                )
            }
        }
    }
    private fun loadFavoritePlaylists() {

        viewModelScope.launch {

            try {

                val user =
                    userRepository.getCurrentUser()

                val favoriteIds =
                    playlistRepository.getFavoritePlaylistIds(
                        userId = user.id
                    )

                _favoritePlaylistIds.value =
                    favoriteIds.toSet()

            } catch (e: Exception) {

                println(
                    "FAVORITE PLAYLIST ERROR: ${e.message}"
                )
            }
        }
    }

    // 6 + 7: Search + Genre filtering
    val filteredPlaylists = combine(
        _playlists,
        _search,
        _selectedGenre
    ) { playlists, search, genre ->

        playlists.filter { playlist ->

            val matchesSearch =
                search.isBlank() ||
                        playlist.title.contains(search, ignoreCase = true) ||
                        playlist.genre.contains(search, ignoreCase = true) ||
                        playlist.mood.contains(search, ignoreCase = true)

            val matchesGenre =
                genre.isBlank() ||
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

                        "sunny",
                        "clear" ->
                            playlist.weather.equals("Sunny", ignoreCase = true) ||
                                    playlist.weather.equals("Clear", ignoreCase = true)

                        "partly cloudy",
                        "cloudy",
                        "overcast" ->
                            playlist.weather.equals("Cloudy", ignoreCase = true) ||
                                    playlist.weather.equals("Partly cloudy", ignoreCase = true)

                        "rain",
                        "drizzle",
                        "light rain",
                        "heavy rain" ->
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

        val weatherIds = weatherPlaylists
            .map { it.id }
            .toSet()

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
                .filter {
                    it.weather.equals(
                        weather.condition,
                        ignoreCase = true
                    )
                }
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

    fun toggleFavorite(playlistId: String) {

        viewModelScope.launch {

            try {

                val user =
                    userRepository.getCurrentUser()

                val userId = user.id

                val isFavorite =
                    playlistId in _favoritePlaylistIds.value

                if (isFavorite) {

                    playlistRepository.unfavoritePlaylist(
                        userId = userId,
                        playlistId = playlistId
                    )

                    _favoritePlaylistIds.value =
                        _favoritePlaylistIds.value - playlistId

                } else {

                    playlistRepository.favoritePlaylist(
                        userId = userId,
                        playlistId = playlistId
                    )

                    _favoritePlaylistIds.value =
                        _favoritePlaylistIds.value + playlistId
                }

            } catch (e: Exception) {

                println(
                    "FAVORITE ERROR: ${e.message}"
                )
            }
        }
    }

    fun openPlaylist(playlist: Playlist) {

        println("OPEN PLAYLIST CALLED: ${playlist.title}")

        viewModelScope.launch {

            try {
                println("HISTORY: GETTING USER")

                val user =
                    userRepository.getCurrentUser()

                println("HISTORY: USER = ${user.id}")
                println("HISTORY PLAYLIST: ${playlist.id}")

                musicHistoryRepository.addHistory(
                    userId = user.id,
                    playlistId = playlist.id
                )

                println("HISTORY SAVED")

            } catch (e: Exception) {

                println("MUSIC HISTORY ERROR")
                e.printStackTrace()
            }
        }
    }
    fun updateSearch(value: String) {
        _search.value = value
    }


    fun selectGenre(value: String) {
        _selectedGenre.value = value
    }

}