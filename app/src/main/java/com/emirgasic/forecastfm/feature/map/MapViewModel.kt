package com.emirgasic.forecastfm.feature.map

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.data.model.MapRecommendation
import com.emirgasic.forecastfm.data.repository.MapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {

    private val repository = MapRepository()


    private val _recommendations =
        MutableStateFlow<List<MapRecommendation>>(emptyList())

    val recommendations: StateFlow<List<MapRecommendation>> =
        _recommendations.asStateFlow()



    private val _selectedRecommendation =
        MutableStateFlow<MapRecommendation?>(null)

    val selectedRecommendation: StateFlow<MapRecommendation?> =
        _selectedRecommendation.asStateFlow()



    init {
        loadRecommendations()
    }



    private fun loadRecommendations() {

        val locations = repository.getRecommendations()

        _recommendations.value = locations

        _selectedRecommendation.value =
            locations.firstOrNull()
    }



    fun selectLocation(location: String) {

        val recommendation =
            repository.getRecommendation(location)

        _selectedRecommendation.value = recommendation
    }
}