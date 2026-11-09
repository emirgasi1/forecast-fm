package com.emirgasic.forecastfm.feature.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SavedPlacesViewModel(
    placeRepository: PlaceRepository
) : ViewModel() {

    // Combines the saved IDs from Room with the actual place details
    val savedPlaces: StateFlow<List<Place>> = combine(
        placeRepository.getSavedPlaces(),
        placeRepository.getLocalPlaces()
    ) { savedIds, allPlaces ->
        allPlaces.filter { place -> savedIds.contains(place.id) }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

}