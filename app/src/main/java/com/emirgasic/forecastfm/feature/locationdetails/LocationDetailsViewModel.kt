package com.emirgasic.forecastfm.feature.locationdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.LocationDetailsUi
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationDetailsViewModel : ViewModel() {

    private val locationRepository = LocationRepository()
    private val weatherRepository = WeatherRepository()
    private val playlistRepository = PlaylistRepository(PlaylistApi())
    private val outfitRepository = OutfitRepository()

    private val _locationDetails = MutableStateFlow<LocationDetailsUi?>(null)
    val locationDetails = _locationDetails.asStateFlow()

    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits = _outfits.asStateFlow()

    fun loadLocation(locationId: String) {
        viewModelScope.launch {
            try {
                val locations = locationRepository.getLocations()
                val location = locations.firstOrNull { it.id == locationId }
                    ?: return@launch

                val weatherData = weatherRepository.getWeather(
                    location = location.name,
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                val playlist = playlistRepository.getRecommendedPlaylist(
                    location = location.name,
                    weather = weatherData.weather.condition
                )

                // Fetch outfits based on weather
                val outfits = outfitRepository.getOutfitsByWeather(weatherData.weather.condition)

                _locationDetails.value = LocationDetailsUi(
                    location = location,
                    weather = weatherData.weather,
                    playlist = playlist
                )
                _outfits.value = outfits

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}