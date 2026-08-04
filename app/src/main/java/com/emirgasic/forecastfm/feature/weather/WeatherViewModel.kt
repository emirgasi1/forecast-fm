package com.emirgasic.forecastfm.feature.weather

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.data.model.Forecast
import com.emirgasic.forecastfm.data.model.Weather
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()


    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather.asStateFlow()


    private val _hourlyForecast = MutableStateFlow<List<Forecast>>(emptyList())
    val hourlyForecast: StateFlow<List<Forecast>> = _hourlyForecast.asStateFlow()


    private val _dailyForecast = MutableStateFlow<List<Forecast>>(emptyList())
    val dailyForecast: StateFlow<List<Forecast>> = _dailyForecast.asStateFlow()



    init {
        loadWeather()
    }


    private fun loadWeather(){

        _weather.value = repository.getCurrentWeather()

        _hourlyForecast.value = repository.getHourlyForecast()

        _dailyForecast.value = repository.getDailyForecast()

    }
}