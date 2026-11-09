package com.emirgasic.forecastfm.feature.locationdetails.placerecommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaceRecommendationViewModel(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _allPlaces = MutableStateFlow<List<Place>>(emptyList())

    private val _recommendations = MutableStateFlow<List<Place>>(emptyList())
    val recommendations: StateFlow<List<Place>> = _recommendations.asStateFlow()

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

    private var loadedVenueId: String? = null

    fun loadForVenue(venueId: String) {
        if (loadedVenueId == venueId) return
        loadedVenueId = venueId

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allLocalPlaces = placeRepository.getLocalPlaces().first()

                _allPlaces.value = allLocalPlaces

                filterRecommendations()
            } catch (_: Exception) {
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
        val category = _selectedCategory.value.lowercase()
        val companion = _selectedCompanion.value.lowercase()
        val weather = _selectedWeather.value.lowercase()
        val ageGroup = _selectedAgeGroup.value.lowercase()

        val filtered = _allPlaces.value.filter { place ->
            val cat = place.category.lowercase()

            // 1. CATEGORY (Matches if the string is inside the category)
            val categoryMatches = if (category.isEmpty()) true else {
                when (category) {
                    "activity" -> cat in listOf("park", "garden", "viewpoint", "attraction", "sports_centre", "playground")
                    "culture" -> cat in listOf("museum", "gallery", "theatre", "arts_centre", "library", "place_of_worship")
                    "nightlife" -> cat in listOf("bar", "pub", "nightclub", "casino")
                    "shop" -> cat in listOf("shop", "supermarket", "marketplace", "clothes", "bakery")
                    "restaurant" -> cat in listOf("restaurant", "fast_food")
                    "cafe" -> cat in listOf("cafe", "internet_cafe")
                    else -> cat.contains(category)
                }
            }

            // 2. COMPANION (Excludes things that don't fit)
            val companionMatches = if (companion.isEmpty()) true else {
                when (companion) {
                    "family" -> cat !in listOf("nightclub", "casino", "bar", "pub") // Hide adult-only places
                    "partner" -> cat !in listOf("playground", "kindergarten") // Hide kid places
                    "friends" -> true // Friends can go anywhere
                    "colleagues" -> cat in listOf("cafe", "restaurant", "fast_food", "bar", "pub") // Work settings
                    "alone" -> true
                    else -> true
                }
            }

            // 3. WEATHER (Hide outdoor if Rainy/Snowy)
            val weatherMatches = if (weather.isEmpty()) true else {
                val isOutdoor = cat in listOf("park", "garden", "viewpoint", "attraction", "camp_site", "picnic_site", "playground")
                when (weather) {
                    "rainy", "snowy" -> !isOutdoor // Must be indoor
                    "sunny", "clear" -> true // Everything is fine
                    "cloudy" -> true
                    else -> true
                }
            }

            // 4. AGE GROUP (Hide inappropriate places)
            val ageGroupMatches = if (ageGroup.isEmpty()) true else {
                when (ageGroup) {
                    "teens", "young adults" -> cat !in listOf("casino", "nightclub") // Hide casinos
                    "seniors" -> cat !in listOf("nightclub", "casino") // Hide clubs
                    "all ages" -> cat !in listOf("nightclub", "casino", "bar", "pub") // Strictly family friendly
                    else -> true
                }
            }

            val result = categoryMatches && companionMatches && weatherMatches && ageGroupMatches

            // Logcat output to see what's happening
            if (!result) {
                android.util.Log.d("FilterDebug", "Removed ${place.name} (${place.category}) | Cat:$categoryMatches Comp:$companionMatches Weather:$weatherMatches Age:$ageGroupMatches")
            }

            result
        }

        android.util.Log.d("FilterDebug", "Filters: Cat=$category, Comp=$companion, Weather=$weather, Age=$ageGroup -> Kept ${filtered.size} out of ${_allPlaces.value.size}")
        _recommendations.value = filtered.take(20)
    }

    fun resetFilters() {
        _selectedCategory.value = ""
        _selectedCompanion.value = ""
        _selectedWeather.value = ""
        _selectedAgeGroup.value = ""
        filterRecommendations()
    }
}