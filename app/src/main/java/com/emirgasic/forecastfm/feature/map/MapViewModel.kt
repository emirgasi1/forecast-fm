package com.emirgasic.forecastfm.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.BusStation
import com.emirgasic.forecastfm.data.model.MapMarker
import com.emirgasic.forecastfm.data.model.MapRecommendation
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.repository.BusStationRepository
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.MapRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.network.location.LocationResponse
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
class MapViewModel : ViewModel() {

    private val repository = LocationRepository()
    private val outfitRepository = OutfitRepository()
    private val placeRepository = PlaceRepository()
    private val playlistRepository = PlaylistRepository(playlistApi = PlaylistApi())

    private val busStationRepository = BusStationRepository()

    private val _locations = MutableStateFlow<List<LocationResponse>>(emptyList())
    val locations: StateFlow<List<LocationResponse>> = _locations.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LocationResponse?>(null)
    val selectedLocation: StateFlow<LocationResponse?> = _selectedLocation.asStateFlow()

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    private val _selectedPlace = MutableStateFlow<Place?>(null)
    val selectedPlace: StateFlow<Place?> = _selectedPlace.asStateFlow()

    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    private val _playlistMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val playlistMap: StateFlow<Map<String, String>> = _playlistMap.asStateFlow()

    private val _markers = MutableStateFlow<List<MapMarker>>(emptyList())
    val markers: StateFlow<List<MapMarker>> = _markers.asStateFlow()


    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilters = MutableStateFlow<Set<String>>(emptySet())
    val selectedFilters: StateFlow<Set<String>> = _selectedFilters.asStateFlow()

    private val _enabledLayers = MutableStateFlow(setOf("Venues", "Places", "Bus Stops"))

    val enabledLayers: StateFlow<Set<String>> = _enabledLayers.asStateFlow()


    private val _busStations = MutableStateFlow<List<BusStation>>(emptyList())
    val busStations: StateFlow<List<BusStation>> = _busStations.asStateFlow()
    init {
        loadLocations()
        loadBusStations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            try {
                val locations = repository.getLocations()
                _locations.value = locations
                _selectedLocation.value = locations.firstOrNull()
                _selectedLocation.value?.let {
                    loadPlacesForVenue(it.id)
                }
                updateMarkers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadPlacesForVenue(venueId: String) {
        viewModelScope.launch {
            try {
                val places = placeRepository.getPlacesByVenue(venueId)
                _places.value = places
                _selectedPlace.value = places.firstOrNull()
                updateMarkers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectLocation(location: LocationResponse) {
        _selectedLocation.value = location
        loadPlacesForVenue(location.id)
        updateMarkers()
    }

    fun selectPlace(place: Place) {
        _selectedPlace.value = place
        updateMarkers()
    }

    fun loadPlaylistForLocation(locationId: String, weather: String) {
        viewModelScope.launch {
            try {
                val location = _locations.value.firstOrNull { it.id == locationId }
                if (location != null) {
                    val playlist = playlistRepository.getRecommendedPlaylist(
                        location = location.name,
                        weather = weather
                    )
                    _playlistMap.value = _playlistMap.value + (locationId to (playlist?.title ?: "Today's Soundtrack"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadOutfits(weather: String) {
        viewModelScope.launch {
            try {
                val outfits = outfitRepository.getOutfitsByWeather(weather)
                _outfits.value = outfits
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun updateMarkers() {
        val currentLocations = _locations.value
        val currentPlaces = _places.value
        val currentBusStations = _busStations.value
        val currentSelectedLocation = _selectedLocation.value
        val currentSelectedPlace = _selectedPlace.value

        val venueMarkers = currentLocations.map { location ->
            MapMarker(
                id = location.id,
                name = location.name,
                latitude = location.latitude,
                longitude = location.longitude,
                type = "venue",
                category = "",
                isSelected = currentSelectedLocation?.id == location.id
            )
        }

        val placeMarkers = currentPlaces.map { place ->
            MapMarker(
                id = place.id,
                name = place.name,
                latitude = place.latitude,
                longitude = place.longitude,
                type = "place",
                category = place.category,
                isSelected = currentSelectedPlace?.id == place.id
            )
        }

        val busMarkers = currentBusStations.map { station ->
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

        _markers.value = venueMarkers + placeMarkers + busMarkers
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFilter(filter: String) {
        _selectedFilters.value = if (filter in _selectedFilters.value) {
            _selectedFilters.value - filter
        } else {
            _selectedFilters.value + filter
        }
    }

    fun toggleLayer(layer: String) {
        _enabledLayers.value = if (layer in _enabledLayers.value) {
            _enabledLayers.value - layer
        } else {
            _enabledLayers.value + layer
        }
    }
    val filteredMarkers = combine(
        _markers,
        _searchQuery,
        _selectedFilters,
        _enabledLayers
    ) { markers, query, filters, layers ->
        markers.filter { marker ->
            val layerMatch = when (marker.type) {
                "venue" -> "Venues" in layers
                "place" -> "Places" in layers
                "bus_station" -> "Bus Stops" in layers
                else -> true
            }

            val filterMatch = filters.isEmpty() || filters.any { filter ->
                marker.category.equals(filter, ignoreCase = true) ||
                        (filter == "Venues" && marker.type == "venue") ||
                        (filter == "Places" && marker.type == "place") ||
                        (filter == "Bus Stops" && marker.type == "bus_station")
            }

            val searchMatch = query.isBlank() ||
                    marker.name.contains(query, ignoreCase = true)

            layerMatch && filterMatch && searchMatch
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )
    private fun loadBusStations() {
        viewModelScope.launch {
            try {
                val stations = busStationRepository.getAllBusStations()
                _busStations.value = stations
                updateMarkers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}