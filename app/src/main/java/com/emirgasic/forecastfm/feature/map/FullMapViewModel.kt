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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val _filteredMarkers = MutableStateFlow<List<MapMarker>>(emptyList())

    // 🔴 changed to single String
    private val _markerGeoJson = MutableStateFlow("""{"type":"FeatureCollection","features":[]}""")
    val markerGeoJson: StateFlow<String> = _markerGeoJson.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LocationResponse?>(null)
    val selectedLocation: StateFlow<LocationResponse?> = _selectedLocation.asStateFlow()

    private val _selectedPlace = MutableStateFlow<Place?>(null)
    val selectedPlace: StateFlow<Place?> = _selectedPlace.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _enabledLayers = MutableStateFlow(
        setOf(
            "Neighborhoods", "Food & Drink", "Bars & Nightlife", "Shopping",
            "Accommodation", "Health", "Finance & Post", "Culture & Education",
            "Parks & Nature", "Worship", "Transit", "Emergency","Others"
        )
    )

    val enabledLayers: StateFlow<Set<String>> = _enabledLayers.asStateFlow()

    private val _venueCategoryFilter = MutableStateFlow<String?>(null)
    val venueCategoryFilter: StateFlow<String?> = _venueCategoryFilter.asStateFlow()

    // Category -> group mapping (consistent with your original logic)
    private val layerGroups: Map<String, Set<String>> = mapOf(
        "Neighborhoods" to setOf("suburb", "neighbourhood", "quarter", "general"),
        "Food & Drink" to setOf("cafe", "internet_cafe", "restaurant", "fast_food", "bakery"),
        "Bars & Nightlife" to setOf("bar", "pub", "hookah_lounge", "nightclub", "nightlife", "casino"),
        "Shopping" to setOf(
            "shop", "supermarket", "marketplace", "clothes", "electronics",
            "jewelry", "optician", "florist", "variety_store", "kiosk",
            "stationery", "hairdresser", "car_rental", "bicycle_rental"
        ),
        "Accommodation" to setOf("hotel", "hostel", "motel", "guest_house", "apartment", "alpine_hut", "chalet"),
        "Health" to setOf("hospital", "clinic", "pharmacy", "dentist", "doctors", "veterinary"),
        "Finance & Post" to setOf("bank", "atm", "bureau_de_change", "post_office", "post_box", "parcel_locker"),
        "Culture & Education" to setOf(
            "museum", "gallery", "theatre", "cinema", "arts_centre",
            "library", "school", "university", "kindergarten", "college"
        ),
        "Parks & Nature" to setOf("park", "garden", "picnic_site", "camp_site", "viewpoint", "attraction", "artwork"),
        "Worship" to setOf("place_of_worship", "monastery"),
        "Transit" to setOf("bus_stop", "bus_station", "taxi", "fuel", "charging_station", "parking"),
        "Emergency" to setOf("police", "fire_station")
    )

    init {
        // 👈 Observe Room for Locations
        locationRepository.getLocalLocations()
            .onEach { locations ->
                _locations.value = locations
                rebuildMarkers()
            }
            .launchIn(viewModelScope)

        // 👈 Observe Room for Places
        placeRepository.getLocalPlaces()
            .onEach { places ->
                _places.value = places
                rebuildMarkers()
            }
            .launchIn(viewModelScope)

        // 👈 Sync to network in background
        viewModelScope.launch {
            try { locationRepository.syncLocationsFromApi() } catch (_: Exception) {}
            try { placeRepository.syncPlacesFromApi() } catch (_: Exception) {}
        }
    }
    fun loadPlaces() {
        viewModelScope.launch {
            try {
                _places.value = placeRepository.getAllPlaces()
                rebuildMarkers()
            } catch (_: Exception) {}
        }
    }

    fun loadBusStations() {
        viewModelScope.launch {
            try {
                _busStations.value = busStationRepository.getAllBusStations()
                rebuildMarkers()
            } catch (_: Exception) {}
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
                category = location.category,
                isSelected = location.id == _selectedLocation.value?.id
            )
        }

        val placeMarkers = _places.value.map { place ->
            MapMarker(
                id = place.id, name = place.name, latitude = place.latitude, longitude = place.longitude,
                type = "place", category = PlaceRanker.canonicalCategory(place.category) ?: "general",
                isSelected = place.id == _selectedPlace.value?.id
            )
        }

        val busStationMarkers = _busStations.value.map { station ->
            MapMarker(
                id = station.id, name = station.name, latitude = station.latitude, longitude = station.longitude,
                type = "bus_station", category = "bus_stop", isSelected = false
            )
        }

        _filteredMarkers.value = locationMarkers + placeMarkers + busStationMarkers
        applyFilters()
    }

    private fun categoryEnabled(category: String): Boolean {
        val group = layerGroups.entries.firstOrNull { category in it.value }?.key
            ?: "Others"
        return group in _enabledLayers.value
    }

    private fun applyFilters() {
        val query = _searchQuery.value.trim()
        val canonical = PlaceRanker.canonicalCategory(_venueCategoryFilter.value)
        val categories = _filteredMarkers.value.map { it.category }.distinct()
        android.util.Log.d("MapCategories", "Categories: $categories")
        val filtered = _filteredMarkers.value.filter { marker ->
            if (!categoryEnabled(marker.category)) return@filter false
            if (marker.type == "place" && canonical != null && marker.category != canonical) return@filter false
            if (query.isNotEmpty() && !marker.name.contains(query, ignoreCase = true)) return@filter false
            true
        }

        val features = filtered.joinToString(",") { marker ->
            val id = Json.encodeToString(marker.id)
            val name = Json.encodeToString(marker.name)
            val type = Json.encodeToString(marker.type)
            val cat = Json.encodeToString(marker.category)
            val sel = marker.isSelected
            """
            {"type":"Feature",
             "properties":{"id":$id,"name":$name,"type":$type,"category":$cat,"isSelected":$sel},
             "geometry":{"type":"Point","coordinates":[${marker.longitude},${marker.latitude}]}}
            """.trimIndent()
        }
        _markerGeoJson.value = """{"type":"FeatureCollection","features":[$features]}"""
    }

    fun selectLocation(location: LocationResponse) {
        _selectedLocation.value = location
        _selectedPlace.value = null
        rebuildMarkers()
    }

    fun selectPlace(place: Place) {
        _selectedPlace.value = place
        val location = _locations.value.firstOrNull { it.id == place.venueId }
        if (location != null) _selectedLocation.value = location
        rebuildMarkers()
    }

    fun setVenueCategoryFilter(category: String?) {
        _venueCategoryFilter.value = if (category.isNullOrBlank() || category.equals("All", ignoreCase = true)) null else category
        _selectedPlace.value = null
        applyFilters()
    }

    fun toggleLayer(layer: String) {
        val current = _enabledLayers.value.toMutableSet()
        if (layer in current) current.remove(layer) else current.add(layer)
        _enabledLayers.value = current
        applyFilters()
    }
}