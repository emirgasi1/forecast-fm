package com.emirgasic.forecastfm.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlaceSearchViewModel(
    private val repository: PlaceRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<Place>>(emptyList())
    val results: StateFlow<List<Place>> = _results.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var searchJob: Job? = null
    private var allLocalPlaces: List<Place> = emptyList()

    init {
        // 👈 Observe Room continuously in the background
        repository.getLocalPlaces()
            .onEach { places ->
                allLocalPlaces = places
                // If there's already a query, re-filter the list
                if (_query.value.isNotBlank()) {
                    performLocalSearch(_query.value)
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateQuery(value: String) {
        _query.value = value
        searchJob?.cancel()

        if (value.isBlank()) {
            _results.value = emptyList()
            _isLoading.value = false
            _error.value = null
            return
        }

        searchJob = viewModelScope.launch {
            // Add a tiny debounce so we don't filter on every single keystroke instantly
            delay(200)
            _isLoading.value = true
            _error.value = null

            performLocalSearch(value)

            _isLoading.value = false
        }
    }

    private fun performLocalSearch(query: String) {
        // If Room is somehow completely empty (e.g., first launch offline), show an error
        if (allLocalPlaces.isEmpty()) {
            _error.value = "No offline data available"
            _results.value = emptyList()
            return
        }

        // 👈 Instant local search
        val filtered = allLocalPlaces.filter { place ->
            place.name.contains(query, ignoreCase = true) ||
                    place.category.contains(query, ignoreCase = true) ||
                    place.address?.contains(query, ignoreCase = true) == true
        }
        _results.value = filtered.take(50) // Limit to 50 results for the UI
    }

    fun clear() {
        searchJob?.cancel()
        _query.value = ""
        _results.value = emptyList()
        _isLoading.value = false
        _error.value = null
    }
}