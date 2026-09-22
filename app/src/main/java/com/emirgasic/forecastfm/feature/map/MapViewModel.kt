package com.emirgasic.forecastfm.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import com.emirgasic.forecastfm.data.model.BusStation
import com.emirgasic.forecastfm.data.model.MapMarker
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.model.Place
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
            val locationMatch =
                location == null || place.venueId == location.id

            val categoryMatch =
                category == null ||
                        canonicalCategory(place.category) == canonicalCategory(category)

            locationMatch && categoryMatch
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    private val markerLocationCategoryFilter: StateFlow<Pair<LocationResponse?, String?>> =
        combine(
            _selectedLocation,
            _venueCategoryFilter
        ) { location, category ->
            location to category
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            null to null
        )

    val filteredMarkers: StateFlow<List<MapMarker>> =
        combine(
            _markers,
            _searchQuery,
            _selectedFilters,
            _enabledLayers
        ) { markers, query, filters, layers ->

            FilterBaseData(
                markers = markers,
                query = query,
                filters = filters,
                layers = layers
            )
        }.combine(
            combine(
                _selectedLocation,
                _venueCategoryFilter,
                _allPlaces
            ) { location, category, places ->
                FilterLocationData(
                    location = location,
                    category = category,
                    places = places
                )
            }
        ) { base, locationData ->

            val placesById = locationData.places.associateBy { it.id }

            base.markers.filter { marker ->

                val layerMatch = when (marker.type) {
                    "venue" -> "Venues" in base.layers
                    "place" -> "Places" in base.layers
                    "bus_station" -> "Bus Stops" in base.layers
                    else -> true
                }

                val place = placesById[marker.id]

                val locationMatch = when (marker.type) {
                    "venue" ->
                        locationData.location == null ||
                                marker.id == locationData.location.id

                    "place" ->
                        locationData.location == null ||
                                place?.venueId == locationData.location.id

                    else -> true
                }

                val categoryMatch =
                    marker.type != "place" ||
                            locationData.category == null ||
                            canonicalCategory(marker.category) ==
                            canonicalCategory(locationData.category)

                val filterMatch =
                    base.filters.isEmpty() ||
                            base.filters.any { filter: String ->
                                canonicalCategory(marker.category) ==
                                        canonicalCategory(filter) ||
                                        (
                                                filter.equals(
                                                    "Venues",
                                                    ignoreCase = true
                                                ) &&
                                                        marker.type == "venue"
                                                ) ||
                                        (
                                                filter.equals(
                                                    "Places",
                                                    ignoreCase = true
                                                ) &&
                                                        marker.type == "place"
                                                ) ||
                                        (
                                                filter.equals(
                                                    "Bus Stops",
                                                    ignoreCase = true
                                                ) &&
                                                        marker.type == "bus_station"
                                                )
                            }

                val searchMatch =
                    base.query.isBlank() ||
                            marker.name.contains(
                                base.query,
                                ignoreCase = true
                            )

                layerMatch &&
                        locationMatch &&
                        categoryMatch &&
                        filterMatch &&
                        searchMatch
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    init {
        loadLocations()
        loadAllPlaces()
        loadBusStations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            try {
                val locations = locationRepository.getLocations()
                _locations.value = locations
                _selectedLocation.value = locations.firstOrNull()
                updateMarkers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadAllPlaces() {
        viewModelScope.launch {
            try {
                val allPlaces = placeRepository.getAllPlaces()
                val prefs = onboardingPrefs.placeCategories.first()

                val sorted = if (prefs.isEmpty()) {
                    allPlaces.sortedByDescending { it.rating }
                } else {
                    allPlaces.sortedWith(
                        compareByDescending<Place> { scorePlace(it, prefs) }
                            .thenByDescending { it.rating }
                    )
                }

                _allPlaces.value = sorted
                _selectedPlace.value = sorted.firstOrNull()
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

                _selectedPlace.value = places.firstOrNull {
                    _venueCategoryFilter.value == null ||
                            canonicalCategory(it.category) ==
                            canonicalCategory(_venueCategoryFilter.value)
                }

                updateMarkers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectLocation(location: LocationResponse) {
        _selectedLocation.value = location

        viewModelScope.launch {
            _selectedPlace.value = _allPlaces.value.firstOrNull { place ->
                place.venueId == location.id &&
                        (
                                _venueCategoryFilter.value == null ||
                                        canonicalCategory(place.category) ==
                                        canonicalCategory(_venueCategoryFilter.value)
                                )
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
                        _playlistMap.value =
                            _playlistMap.value + (locationId to playlist.title)
                    }
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
        _selectedFilters.value =
            if (filter in _selectedFilters.value) {
                _selectedFilters.value - filter
            } else {
                _selectedFilters.value + filter
            }
    }

    fun toggleLayer(layer: String) {
        _enabledLayers.value =
            if (layer in _enabledLayers.value) {
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
                        (
                                category == null ||
                                        canonicalCategory(place.category) ==
                                        canonicalCategory(category)
                                )
            }
        }

        updateMarkers()
    }

    private fun scorePlace(place: Place, prefs: Set<String>): Int {
        return if (
            prefs.any {
                canonicalCategory(place.category) == canonicalCategory(it)
            }
        ) {
            10
        } else {
            0
        }
    }

    private fun canonicalCategory(category: String?): String {
        return when (category?.trim()?.lowercase()) {
            null -> ""
            "all" -> ""
            "cafe", "cafes" -> "cafe"
            "restaurant", "restaurants" -> "restaurant"
            "shop", "shops", "shopping" -> "shop"
            "nightlife", "nightclub", "nightclubs" -> "nightlife"
            "park", "parks", "parks & nature" -> "park"
            "outdoor", "outdoors" -> "outdoor"
            "viewpoint", "viewpoints" -> "viewpoint"
            "attraction", "attractions" -> "attraction"
            "culture", "cultures", "historic & culture" -> "culture"
            "bus stop", "bus stops" -> "bus stop"
            else -> category.trim().lowercase()
        }
    }

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
    private data class FilterBaseData(
        val markers: List<MapMarker>,
        val query: String,
        val filters: Set<String>,
        val layers: Set<String>
    )

    private data class FilterLocationData(
        val location: LocationResponse?,
        val category: String?,
        val places: List<Place>
    )
}