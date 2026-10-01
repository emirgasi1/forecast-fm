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

    fun loadPlace(placeId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = placeRepository.getPlaceById(placeId)
                _place.value = result

                result?.venueId?.let { venueId ->
                    val location = locationRepository.getLocationById(venueId)
                    _locationName.value = location?.name
                }

                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    _isSaved.value = savedPlaceRepository.isPlaceSaved(placeId, userId)
                }
            } catch (_: Exception) {
                // Silent failure — screen shows "place not found" state
            }
            _isLoading.value = false
        }
    }

    fun toggleSave() {
        viewModelScope.launch {
            try {
                val placeId = _place.value?.id ?: return@launch
                val userId = tokenManager.getUserId().first() ?: return@launch

                if (_isSaved.value) {
                    savedPlaceRepository.unsavePlace(placeId, userId)
                    _isSaved.value = false
                } else {
                    savedPlaceRepository.savePlace(placeId, userId)
                    _isSaved.value = true
                }
            } catch (_: Exception) {
                // Silent failure — state unchanged
            }
        }
    }
}