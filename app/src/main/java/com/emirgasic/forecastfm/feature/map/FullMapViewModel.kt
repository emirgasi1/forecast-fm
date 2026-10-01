package com.emirgasic.forecastfm.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.BusStation
import com.emirgasic.forecastfm.data.model.MapMarker
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.recommender.PlaceRanker
import com.emirgasic.forecastfm.data.repository.BusStationRepository
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import com.emirgasic.forecastfm.network.location.LocationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class FullMapViewModel(
    private val locationRepository: LocationRepository,
    private val placeRepository: PlaceRepository,
    private val busStationRepository: BusStationRepository
) : ViewModel() {

    private val _locations = MutableStateFlow<List<LocationResponse>>(emptyList())
    val locations: StateFlow<List<LocationResponse>> = _locations.asStateFlow()

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    private val _busStations = MutableStateFlow<List<BusStation>>(emptyList())
    val busStations: StateFlow<List<BusStation>> = _busStations.asStateFlow()

    private val _markers = MutableStateFlow<List<MapMarker>>(emptyList())
    val markers: StateFlow<List<MapMarker>> = _markers.asStateFlow()

    private val _filteredMarkers = MutableStateFlow<List<MapMarker>>(emptyList())
    val filteredMarkers: StateFlow<List<MapMarker>> = _filteredMarkers.asStateFlow()

    private val _markerGeoJson = MutableStateFlow(
        """{"type":"FeatureCollection","features":[]}"""
    )
    val markerGeoJson: StateFlow<String> = _markerGeoJson.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LocationResponse?>(null)
    val selectedLocation: StateFlow<LocationResponse?> = _selectedLocation.asStateFlow()

    private val _selectedPlace = MutableStateFlow<Place?>(null)
    val selectedPlace: StateFlow<Place?> = _selectedPlace.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilters = MutableStateFlow<Set<String>>(emptySet())
    val selectedFilters: StateFlow<Set<String>> = _selectedFilters.asStateFlow()

    private val _enabledLayers = MutableStateFlow(setOf("Venues", "Places"))
    val enabledLayers: StateFlow<Set<String>> = _enabledLayers.asStateFlow()

    private val _venueCategoryFilter = MutableStateFlow<String?>(null)
    val venueCategoryFilter: StateFlow<String?> = _venueCategoryFilter.asStateFlow()

    fun loadLocations() {
        viewModelScope.launch {
            try {
                _locations.value = locationRepository.getLocations()
                rebuildMarkers()
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }

    fun loadPlaces() {
        viewModelScope.launch {
            try {
                _places.value = placeRepository.getAllPlaces()
                rebuildMarkers()
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }

    fun loadBusStations() {
        viewModelScope.launch {
            try {
                _busStations.value = busStationRepository.getAllBusStations()
                rebuildMarkers()
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }

    private fun rebuildMarkers() {
        val locationMarkers = _locations.value.map { location ->
            MapMarker(
                id = location.id,
                name = location.name,
                latitude = location.latitude,
                longitude = location.longitude,
                type = "venue",
                category = "",
                isSelected = location.id == _selectedLocation.value?.id
            )
        }

        val placeMarkers = _places.value.map { place ->
            MapMarker(
                id = place.id,
                name = place.name,
                latitude = place.latitude,
                longitude = place.longitude,
                type = "place",
                category = place.category,
                isSelected = place.id == _selectedPlace.value?.id
            )
        }

        val busStationMarkers = _busStations.value.map { station ->
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

        _markers.value = locationMarkers + placeMarkers + busStationMarkers
        applyFilters()
    }

    private fun applyFilters() {
        val query = _searchQuery.value.trim()
        val canonical = PlaceRanker.canonicalCategory(_venueCategoryFilter.value)

        val filtered = _markers.value.filter { marker ->
            val layerVisible = when (marker.type) {
                "venue" -> "Venues" in _enabledLayers.value
                "place" -> "Places" in _enabledLayers.value
                "bus_station" -> "Bus Stops" in _enabledLayers.value
                else -> true
            }
            if (!layerVisible) return@filter false

            val categoryVisible = when (marker.type) {
                "place" -> canonical == null ||
                        PlaceRanker.canonicalCategory(marker.category) == canonical
                else -> true
            }
            if (!categoryVisible) return@filter false

            val searchVisible = query.isEmpty() ||
                    marker.name.contains(query, ignoreCase = true)
            if (!searchVisible) return@filter false

            true
        }

        _filteredMarkers.value = filtered
        _markerGeoJson.value = buildMarkerGeoJson(filtered)
    }

    private fun buildMarkerGeoJson(markers: List<MapMarker>): String {
        val features = markers.joinToString(",") { marker ->
            val id = Json.encodeToString(marker.id)
            val name = Json.encodeToString(marker.name)
            val type = Json.encodeToString(marker.type)
            val category = Json.encodeToString(marker.category)
            val colorKey = Json.encodeToString(computeColorKey(marker))

            """
            {
                "type": "Feature",
                "properties": {
                    "id": $id,
                    "name": $name,
                    "type": $type,
                    "category": $category,
                    "colorKey": $colorKey
                },
                "geometry": {
                    "type": "Point",
                    "coordinates": [
                        ${marker.longitude},
                        ${marker.latitude}
                    ]
                }
            }
            """.trimIndent()
        }

        return """
            {
                "type": "FeatureCollection",
                "features": [$features]
            }
        """.trimIndent()
    }

    private fun computeColorKey(marker: MapMarker): String {
        if (marker.isSelected && marker.type == "venue") return "selected_venue"
        if (marker.isSelected && marker.type == "place") return "selected_place"

        return when (PlaceRanker.canonicalCategory(marker.category)) {
            "cafe" -> "cafe"
            "restaurant" -> "restaurant"
            "bar" -> "bar"
            "nightlife" -> "nightlife"
            "park" -> "park"
            "museum" -> "museum"
            "shop" -> "shop"
            "hotel" -> "hotel"
            "landmark" -> "landmark"
            "culture" -> "culture"
            "activity" -> "activity"
            "outdoor" -> "outdoor"
            "viewpoint" -> "viewpoint"
            "attraction" -> "attraction"
            "bus stop" -> "bus_stop"
            else -> "default"
        }
    }

    fun selectLocation(location: LocationResponse) {
        _selectedLocation.value = location
        _selectedPlace.value = null
        rebuildMarkers()
    }

    fun selectPlace(place: Place) {
        _selectedPlace.value = place

        val location = _locations.value.firstOrNull { it.id == place.venueId }
        if (location != null) {
            _selectedLocation.value = location
        }

        rebuildMarkers()
    }

    fun setVenueCategoryFilter(category: String?) {
        _venueCategoryFilter.value =
            if (category.isNullOrBlank() || category.equals("All", ignoreCase = true)) {
                null
            } else {
                category
            }

        _selectedPlace.value = null
        applyFilters()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun toggleFilter(filter: String) {
        val current = _selectedFilters.value.toMutableSet()
        if (filter in current) current.remove(filter) else current.add(filter)
        _selectedFilters.value = current
        applyFilters()
    }

    fun toggleLayer(layer: String) {
        val current = _enabledLayers.value.toMutableSet()
        if (layer in current) current.remove(layer) else current.add(layer)
        _enabledLayers.value = current
        applyFilters()
    }
}