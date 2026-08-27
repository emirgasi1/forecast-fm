package com.emirgasic.forecastfm.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.Forecast
import com.emirgasic.forecastfm.data.model.Weather
import com.emirgasic.forecastfm.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather.asStateFlow()

    private val _hourlyForecast =
        MutableStateFlow<List<Forecast>>(emptyList())

    val hourlyForecast: StateFlow<List<Forecast>> =
        _hourlyForecast.asStateFlow()

    private val _dailyForecast =
        MutableStateFlow<List<Forecast>>(emptyList())

    val dailyForecast: StateFlow<List<Forecast>> =
        _dailyForecast.asStateFlow()

    private val _uiState =
        MutableStateFlow(WeatherUiState.LOADING)

    val uiState: StateFlow<WeatherUiState> =
        _uiState.asStateFlow()

    init {
        loadWeather()
    }

    fun loadWeather() {

        viewModelScope.launch {

            _uiState.value = WeatherUiState.LOADING

            try {

                val weatherData = repository.getWeather()

                _weather.value = weatherData.weather
                _hourlyForecast.value = weatherData.hourly
                _dailyForecast.value = weatherData.daily

                _uiState.value = WeatherUiState.SUCCESS

            } catch (e: Exception) {

                e.printStackTrace()

                _uiState.value = WeatherUiState.ERROR
            }
        }
    }
}