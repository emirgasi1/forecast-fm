package com.emirgasic.forecastfm.feature.settings.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.network.location.LocationApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DefaultLocationViewModel : ViewModel() {

    private val locationApi = LocationApi()

    private val _locations = MutableStateFlow<List<String>>(emptyList())
    val locations: StateFlow<List<String>> = _locations.asStateFlow()

    private val _selected = MutableStateFlow("")
    val selected: StateFlow<String> = _selected.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            try {
                val response = locationApi.getLocations()
                val names = response.map { it.name }
                _locations.value = names
                if (_selected.value.isBlank()) {
                    _selected.value = names.firstOrNull() ?: ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun select(name: String) {
        _selected.value = name
    }
}