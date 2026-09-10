package com.emirgasic.forecastfm.feature.locationdetails.placerecommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.PlaceRecommendation
import com.emirgasic.forecastfm.data.repository.PlaceRecommendationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaceRecommendationViewModel : ViewModel() {

    private val repository = PlaceRecommendationRepository()

    private val _allRecommendations = MutableStateFlow<List<PlaceRecommendation>>(emptyList())
    private val _recommendations = MutableStateFlow<List<PlaceRecommendation>>(emptyList())
    val recommendations: StateFlow<List<PlaceRecommendation>> = _recommendations.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedCompanion = MutableStateFlow("")
    val selectedCompanion: StateFlow<String> = _selectedCompanion.asStateFlow()

    private val _selectedWeather = MutableStateFlow("")
    val selectedWeather: StateFlow<String> = _selectedWeather.asStateFlow()

    private val _selectedAgeGroup = MutableStateFlow("")
    val selectedAgeGroup: StateFlow<String> = _selectedAgeGroup.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadRecommendations()
    }

    fun loadRecommendations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = repository.getRecommendations()
                _allRecommendations.value = results
                _recommendations.value = results
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _isLoading.value = false
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = if (_selectedCategory.value == category) "" else category
        filterRecommendations()
    }

    fun selectCompanion(companion: String) {
        _selectedCompanion.value = if (_selectedCompanion.value == companion) "" else companion
        filterRecommendations()
    }

    fun selectWeather(weather: String) {
        _selectedWeather.value = if (_selectedWeather.value == weather) "" else weather
        filterRecommendations()
    }

    fun selectAgeGroup(ageGroup: String) {
        _selectedAgeGroup.value = if (_selectedAgeGroup.value == ageGroup) "" else ageGroup
        filterRecommendations()
    }

    private fun filterRecommendations() {
        val filtered = _allRecommendations.value.filter { place ->
            val categoryMatches = _selectedCategory.value.isEmpty() ||
                    place.category.equals(_selectedCategory.value, ignoreCase = true)

            val companionMatches = _selectedCompanion.value.isEmpty() ||
                    place.suitableFor.any { it.equals(_selectedCompanion.value, ignoreCase = true) }

            val weatherMatches = _selectedWeather.value.isEmpty() ||
                    place.weatherCondition.equals(_selectedWeather.value, ignoreCase = true) ||
                    place.weatherCondition.equals("Any", ignoreCase = true)

            val ageGroupMatches = _selectedAgeGroup.value.isEmpty() ||
                    place.ageGroup.any { it.equals(_selectedAgeGroup.value, ignoreCase = true) } ||
                    place.ageGroup.contains("All Ages")

            categoryMatches && companionMatches && weatherMatches && ageGroupMatches
        }
        _recommendations.value = filtered
    }

    fun resetFilters() {
        _selectedCategory.value = ""
        _selectedCompanion.value = ""
        _selectedWeather.value = ""
        _selectedAgeGroup.value = ""
        _recommendations.value = _allRecommendations.value
    }
}