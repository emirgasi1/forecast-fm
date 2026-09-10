package com.emirgasic.forecastfm.feature.locationdetails.placerecommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.PlaceRecommendation
import com.emirgasic.forecastfm.data.repository.PlaceRecommendationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaceRecommendationDetailViewModel : ViewModel() {

    private val repository = PlaceRecommendationRepository()

    private val _recommendation = MutableStateFlow<PlaceRecommendation?>(null)
    val recommendation: StateFlow<PlaceRecommendation?> = _recommendation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadRecommendation(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getRecommendationById(id)
                _recommendation.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _isLoading.value = false
        }
    }
}