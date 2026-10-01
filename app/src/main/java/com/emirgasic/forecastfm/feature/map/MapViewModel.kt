package com.emirgasic.forecastfm.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import com.emirgasic.forecastfm.data.model.BusStation
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

    private val _markers = MutableStateFlow<List<MapMarker>>(emptyList())
    val markers: StateFlow<List<MapMarker>> = _markers.asStateFlow()

    private val _busStations = MutableStateFlow<List<BusStation>>(emptyList())
    val busStations: StateFlow<List<BusStation>> = _busStations.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilters = MutableStateFlow<Set<String>>(emptySet())
    val selectedFilters: StateFlow<Set<String>> = _selectedFilters.asStateFlow()

    private val _enabledLayers = MutableStateFlow(setOf("Venues", "Places", "Bus Stops"))
    val enabledLayers: StateFlow<Set<String>> = _enabledLayers.asStateFlow()

    private val _venueCategoryFilter = MutableStateFlow<String?>(null)
    val venueCategoryFilter: StateFlow<String?> = _venueCategoryFilter.asStateFlow()

    val visiblePlaces: StateFlow<List<Place>> = combine(
        _allPlaces,
        _selectedLocation,
        _venueCategoryFilter
    ) { places, location, category ->
        places.filter { place ->
            val locationMatch = location == null || place.venueId == location.id
            val categoryMatch = category == null ||
                    PlaceRanker.categoriesMatch(place.category, category)
            locationMatch && categoryMatch
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val filteredMarkers: StateFlow<List<MapMarker>> =
        combine(
            _markers,
            _searchQuery,
            _selectedFilters,
            _enabledLayers,
            _selectedLocation,
            _venueCategoryFilter,
            _allPlaces
        ) { values ->
            val markers = values[0] as List<MapMarker>
            val query = values[1] as String
            val filters = values[2] as Set<String>
            val layers = values[3] as Set<String>
            val location = values[4] as LocationResponse?
            val category = values[5] as String?
            val places = values[6] as List<Place>

            val placesById = places.associateBy { it.id }

            markers.filter { marker ->
                val layerMatch = when (marker.type) {
                    "venue" -> "Venues" in layers
                    "place" -> "Places" in layers
                    "bus_station" -> "Bus Stops" in layers
                    else -> true
                }

                val place = placesById[marker.id]

                val locationMatch = when (marker.type) {
                    "venue" -> location == null || marker.id == location.id
                    "place" -> location == null || place?.venueId == location.id
                    else -> true
                }

                val categoryMatch = marker.type != "place" ||
                        category == null ||
                        PlaceRanker.categoriesMatch(marker.category, category)

                val filterMatch = filters.isEmpty() ||
                        filters.any { filter ->
                            PlaceRanker.categoriesMatch(marker.category, filter) ||
                                    (filter.equals("Venues", true) && marker.type == "venue") ||
                                    (filter.equals("Places", true) && marker.type == "place") ||
                                    (filter.equals("Bus Stops", true) && marker.type == "bus_station")
                        }

                val searchMatch = query.isBlank() ||
                        marker.name.contains(query, ignoreCase = true)

                layerMatch && locationMatch && categoryMatch && filterMatch && searchMatch
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun loadLocations() {
        viewModelScope.launch {
            try {
                val locations = locationRepository.getLocations()
                _locations.value = locations
                _selectedLocation.value = locations.firstOrNull()
                updateMarkers()
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }

    fun loadAllPlaces() {
        viewModelScope.launch {
            try {
                val allPlaces = placeRepository.getAllPlaces()
                val prefs = onboardingPrefs.placeCategories.first()
                val sorted = PlaceRanker.rankByPreferences(allPlaces, prefs)

                _allPlaces.value = sorted
                _selectedPlace.value = sorted.firstOrNull()
                updateMarkers()
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }

    fun loadBusStations() {
        viewModelScope.launch {
            try {
                val stations = busStationRepository.getAllBusStations()
                _busStations.value = stations
                updateMarkers()
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }

    fun loadPlacesForVenue(venueId: String) {
        viewModelScope.launch {
            try {
                val places = placeRepository.getPlacesByVenue(venueId)

                _selectedPlace.value = places.firstOrNull {
                    _venueCategoryFilter.value == null ||
                            PlaceRanker.categoriesMatch(it.category, _venueCategoryFilter.value)
                }

                updateMarkers()
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }

    fun selectLocation(location: LocationResponse) {
        _selectedLocation.value = location

        viewModelScope.launch {
            _selectedPlace.value = _allPlaces.value.firstOrNull { place ->
                place.venueId == location.id &&
                        (_venueCategoryFilter.value == null ||
                                PlaceRanker.categoriesMatch(place.category, _venueCategoryFilter.value))
            }
        }

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
                    if (playlist != null) {
                        _playlistMap.value = _playlistMap.value + (locationId to playlist.title)
                    }
                }
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }

    fun loadOutfits(weather: String) {
        viewModelScope.launch {
            try {
                _outfits.value = outfitRepository.getOutfitsByWeather(weather)
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }

    private fun updateMarkers() {
        val currentLocations = _locations.value
        val currentPlaces = _allPlaces.value
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

    fun setVenueCategoryFilter(category: String?) {
        _venueCategoryFilter.value = category

        viewModelScope.launch {
            val currentLocation = _selectedLocation.value
            _selectedPlace.value = _allPlaces.value.firstOrNull { place ->
                place.venueId == currentLocation?.id &&
                        (category == null ||
                                PlaceRanker.categoriesMatch(place.category, category))
            }
        }

        updateMarkers()
    }
}