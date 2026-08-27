package com.emirgasic.forecastfm.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.Home
import com.emirgasic.forecastfm.data.repository.HomeRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import com.emirgasic.forecastfm.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val homeRepository = HomeRepository()
    private val weatherRepository = WeatherRepository()
    private val playlistRepository = PlaylistRepository(
        playlistApi = PlaylistApi()
    )
    private val _uiState =
        MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    val uiState: StateFlow<HomeUiState> =
        _uiState.asStateFlow()


    init {
        loadHome()
    }


    fun loadHome() {

        viewModelScope.launch {

            _uiState.value = HomeUiState.Loading

            try {

                val weatherData =
                    weatherRepository.getWeather()
                val playlists =
                    playlistRepository.getPlaylists()

                val home = Home(
                    greeting = homeRepository.getGreeting(),
                    weather = weatherData.weather,
                    forecast = weatherData.daily,
                    playlists = playlists
                )

                _uiState.value =
                    HomeUiState.Success(home)

            } catch (e: Exception) {

                e.printStackTrace()

                _uiState.value =
                    HomeUiState.Error(
                        "Unable to load home data"
                    )
            }
        }
    }
}