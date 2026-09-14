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
import kotlinx.coroutines.launch

class PlaceSearchViewModel : ViewModel() {

    private val repository = PlaceRepository()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<Place>>(emptyList())
    val results: StateFlow<List<Place>> = _results.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var searchJob: Job? = null

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
            delay(300)
            _isLoading.value = true
            _error.value = null

            try {
                val response = repository.searchPlaces(value)
                _results.value = response
            } catch (e: Exception) {
                _error.value = e.message ?: "Search failed"
                _results.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clear() {
        searchJob?.cancel()
        _query.value = ""
        _results.value = emptyList()
        _isLoading.value = false
        _error.value = null
    }
}