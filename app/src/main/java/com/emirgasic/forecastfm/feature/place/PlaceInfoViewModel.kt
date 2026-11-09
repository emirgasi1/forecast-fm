package com.emirgasic.forecastfm.feature.place

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import com.emirgasic.forecastfm.data.repository.SavedPlaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlaceInfoViewModel(
    private val tokenManager: TokenManager,
    private val placeRepository: PlaceRepository,
    private val savedPlaceRepository: SavedPlaceRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _place = MutableStateFlow<Place?>(null)
    val place: StateFlow<Place?> = _place.asStateFlow()

    private val _locationName = MutableStateFlow<String?>(null)
    val locationName: StateFlow<String?> = _locationName.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentPlaceId: String? = null

    fun loadPlace(placeId: String) {
        currentPlaceId = placeId
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Try to load from Room first (Offline-first)
                val localPlaces = placeRepository.getLocalPlaces().first()
                val localPlace = localPlaces.firstOrNull { it.id == placeId }

                if (localPlace != null) {
                    _place.value = localPlace
                    // We can still try to refresh from network in the background if we want,
                    // but for now, if it's in Room, we're good.
                } else {
                    // 2. If not in Room, fetch from API
                    val result = placeRepository.getPlaceById(placeId)
                    _place.value = result
                }

                // 3. Load Location Name
                _place.value?.venueId?.let { venueId ->
                    val location = locationRepository.getLocationById(venueId)
                    _locationName.value = location?.name
                }

                // 4. Check if it's saved in Room
                savedPlaceRepository.isPlaceSavedLocally(placeId)
                    .onEach { saved ->
                        _isSaved.value = saved
                    }
                    .launchIn(viewModelScope)

            } catch (e: Exception) {
                // Silent failure
            }
            _isLoading.value = false
        }
    }

    fun toggleSave() {
        viewModelScope.launch {
            try {
                val placeId = currentPlaceId ?: return@launch
                val currentlySaved = _isSaved.value
                val userId = tokenManager.getUserId().first() ?: return@launch

                savedPlaceRepository.toggleSavePlace(placeId, userId, currentlySaved)

            } catch (e: Exception) {
            }
        }
    }
}