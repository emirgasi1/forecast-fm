package com.emirgasic.forecastfm.feature.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import com.emirgasic.forecastfm.data.model.BusStation
import com.emirgasic.forecastfm.data.model.LayerGroups
import com.emirgasic.forecastfm.data.model.MapMarker
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.recommender.PlaceRanker
import com.emirgasic.forecastfm.data.repository.BusStationRepository
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.network.location.LocationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapViewModel(
    private val onboardingPrefs: OnboardingPreferences,
    private val locationRepository: LocationRepository,
    private val outfitRepository: OutfitRepository,
    private val placeRepository: PlaceRepository,
    private val playlistRepository: PlaylistRepository,
    private val busStationRepository: BusStationRepository
) : ViewModel() {

    private val _locations = MutableStateFlow<List<LocationResponse>>(emptyList())
    val locations: StateFlow<List<LocationResponse>> = _locations.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LocationResponse?>(null)
    val selectedLocation: StateFlow<LocationResponse?> = _selectedLocation.asStateFlow()

    private val _allPlaces = MutableStateFlow<List<Place>>(emptyList())

    private val _selectedPlace = MutableStateFlow<Place?>(null)
    val selectedPlace: StateFlow<Place?> = _selectedPlace.asStateFlow()

    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    private val _playlistMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val playlistMap: StateFlow<Map<String, String>> = _playlistMap.asStateFlow()

    private val _busStations = MutableStateFlow<List<BusStation>>(emptyList())
    val busStations: StateFlow<List<BusStation>> = _busStations.asStateFlow()

    private val _venueCategoryFilter = MutableStateFlow<String?>(null)
    val venueCategoryFilter: StateFlow<String?> = _venueCategoryFilter.asStateFlow()

    init {
        observeLocalLocations()
        observeLocalPlaces()
        viewModelScope.launch {
            locationRepository.syncLocationsFromApi()
            placeRepository.syncPlacesFromApi()
        }
    }
    private fun observeLocalLocations() {
        locationRepository.getLocalLocations()
            .onEach { locations ->
                _locations.value = locations
                if (_selectedLocation.value == null) {
                    _selectedLocation.value = locations.firstOrNull()
                }
            }
            .launchIn(viewModelScope)
    }
    private fun observeLocalPlaces() {
        placeRepository.getLocalPlaces()
            .onEach { places ->
                _allPlaces.value = places
                if (_selectedPlace.value == null) {
                    _selectedPlace.value = places.firstOrNull()
                }
            }
            .launchIn(viewModelScope)
    }


    val filteredPlaces: StateFlow<List<Place>> = combine(
        _allPlaces,
        _selectedLocation,
        _venueCategoryFilter
    ) { places, location, category ->

        val inLocation = if (location == null) {
            places
        } else {
            val exactMatches = places.filter { place -> place.venueId == location.id }
            if (exactMatches.isEmpty()) places else exactMatches
        }

        if (category == null) {
            inLocation
        } else {
            inLocation.filter { place ->
                LayerGroups.matches(place.category, category)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val filteredMarkers: StateFlow<List<MapMarker>> = combine(
        filteredPlaces,
        _busStations
    ) { places, stations ->

        val placeMarkers = places.map { place ->
            MapMarker(
                id = place.id,
                name = place.name,
                latitude = place.latitude,
                longitude = place.longitude,
                type = "place",
                category = place.category,
                isSelected = _selectedPlace.value?.id == place.id
            )
        }

        val busMarkers = stations.map { station ->
            MapMarker(
                id = station.id,
                name = station.name,
                latitude = station.latitude,
                longitude = station.longitude,
                type = "bus_station",
                category = "Bus Stops",
                isSelected = false
            )
        }

        (placeMarkers + busMarkers)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun loadAllPlaces() {
        viewModelScope.launch {
            try {
                val allPlaces = placeRepository.getAllPlaces()
                val prefs = onboardingPrefs.placeCategories.first()
                val sorted = PlaceRanker.rankByPreferences(allPlaces, prefs)

                _allPlaces.value = sorted
                _selectedPlace.value = sorted.firstOrNull()
            } catch (e: Exception) {
                Log.e("MapDebug", "loadAllPlaces failed", e)
            }
        }
    }

    fun loadBusStations() {
        viewModelScope.launch {
            try {
                _busStations.value = busStationRepository.getAllBusStations()
            } catch (e: Exception) {
                Log.e("MapDebug", "loadBusStations failed", e)
            }
        }
    }

    fun selectLocation(location: LocationResponse) {
        _selectedLocation.value = location
        _selectedPlace.value = null
    }

    fun selectPlace(place: Place) {
        _selectedPlace.value = place
    }

    fun loadPlaylistForLocation(locationId: String, weather: String) {
        viewModelScope.launch {
            try {
                val location = _locations.value.firstOrNull { it.id == locationId }
                if (location != null) {
                    // 1. Get from Room (offline-first)
                    val localPlaylists = playlistRepository.getLocalPlaylists().first()
                    val playlist = localPlaylists.firstOrNull {
                        it.location.equals(location.name, ignoreCase = true) &&
                                it.weather.equals(weather, ignoreCase = true)
                    } ?: localPlaylists.firstOrNull {
                        it.location.equals(location.name, ignoreCase = true)
                    }

                    if (playlist != null) {
                        _playlistMap.value = _playlistMap.value + (locationId to playlist.title)
                    }
                }
            } catch (e: Exception) {
                Log.e("MapDebug", "loadPlaylist failed", e)
            }
        }
    }

    fun loadOutfits(weather: String) {
        viewModelScope.launch {
            try {

                _outfits.value = outfitRepository.getOutfitsByWeather(weather)
            } catch (e: Exception) {
                Log.e("MapDebug", "loadOutfits failed (probably offline)", e)
            }
        }
    }

    fun setVenueCategoryFilter(category: String?) {
        _venueCategoryFilter.value = category
    }
}