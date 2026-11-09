package com.emirgasic.forecastfm.feature.locationdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.LocationDetailsUi
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LocationDetailsViewModel(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val playlistRepository: PlaylistRepository,
    private val outfitRepository: OutfitRepository,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _locationDetails = MutableStateFlow<LocationDetailsUi?>(null)
    val locationDetails: StateFlow<LocationDetailsUi?> = _locationDetails.asStateFlow()

    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    private val _placesCount = MutableStateFlow(0)
    val placesCount: StateFlow<Int> = _placesCount.asStateFlow()

    fun loadLocation(locationId: String) {
        viewModelScope.launch {
            try {
                // 1. Read location from Room
                val locations = locationRepository.getLocalLocations().first()
                val location = locations.firstOrNull { it.id == locationId }
                    ?: return@launch

                // 2. Fetch Weather (DataStore cache)
                val weatherData = weatherRepository.getWeather(
                    location = location.name,
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                // 3. Fetch Playlist from Room
                val allPlaylists = playlistRepository.getLocalPlaylists().first()
                val playlist = allPlaylists.firstOrNull {
                    it.location.equals(location.name, ignoreCase = true) &&
                            it.weather.equals(weatherData.weather.condition, ignoreCase = true)
                } ?: allPlaylists.firstOrNull {
                    it.location.equals(location.name, ignoreCase = true)
                }

                // 4. Fetch Outfits (Safely handle offline)
                try {
                    _outfits.value = outfitRepository.getOutfitsByWeather(weatherData.weather.condition)
                } catch (_: Exception) {
                    _outfits.value = emptyList() // Keep empty if offline
                }

                // 5. Fetch Places count from Room!
                val localPlaces = placeRepository.getLocalPlaces().first()
                _placesCount.value = localPlaces.count { it.venueId == location.id }

                // 6. Update UI State
                _locationDetails.value = LocationDetailsUi(
                    location = location,
                    weather = weatherData.weather,
                    playlist = playlist
                )

            } catch (e: Exception) {
                // If weather or location fails, it will fall back to the catch
            }
        }
    }
}