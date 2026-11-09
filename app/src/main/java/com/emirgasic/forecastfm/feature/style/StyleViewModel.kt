package com.emirgasic.forecastfm.feature.style

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.local.datastore.WeatherCacheManager
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.model.Style
import com.emirgasic.forecastfm.data.model.WeatherCondition
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.SavedOutfitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StyleViewModel(
    private val tokenManager: TokenManager,
    private val outfitRepository: OutfitRepository,
    private val savedOutfitRepository: SavedOutfitRepository,
    private val weatherCacheManager: WeatherCacheManager
) : ViewModel() {

    private val _style = MutableStateFlow<Style?>(null)
    val style: StateFlow<Style?> = _style.asStateFlow()

    private val _savedOutfitIds = MutableStateFlow<Set<String>>(emptySet())
    val savedOutfitIds: StateFlow<Set<String>> = _savedOutfitIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentCondition = MutableStateFlow(WeatherCondition.UNKNOWN)
    val currentCondition: StateFlow<WeatherCondition> = _currentCondition.asStateFlow()

    init {
        savedOutfitRepository.getSavedOutfitIds()
            .onEach { ids ->
                _savedOutfitIds.value = ids.toSet()
            }
            .launchIn(viewModelScope)
    }

    fun loadStyle() {
        viewModelScope.launch {
            if (_style.value == null) {
                _isLoading.value = true
            }
            _error.value = null

            try {
                val weatherData = weatherCacheManager.getCachedWeatherData()
                val condition = WeatherCondition.from(weatherData?.weather?.condition)
                _currentCondition.value = condition

                val allOutfits = outfitRepository.getGroupedOutfits().values.flatten()

                val filteredOutfits = if (condition.bucket == WeatherCondition.Bucket.ALL) {
                    allOutfits
                } else {
                    val matches = allOutfits.filter {
                        WeatherCondition.from(it.weatherCondition).bucket == condition.bucket
                    }
                    matches.ifEmpty { allOutfits }
                }

                _style.value = Style(outfits = filteredOutfits)
            } catch (e: Exception) {
                _error.value = e.message
            }

            _isLoading.value = false
        }
    }

    fun toggleSaveOutfit(outfitId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first() ?: return@launch
                val isSaved = _savedOutfitIds.value.contains(outfitId)

                // 1. Update the UI instantly
                savedOutfitRepository.toggleSaveOutfit(outfitId, userId, isSaved)

                // 2. If we are liking it (not un-liking), increment the like count locally
                if (!isSaved) {
                    val currentOutfits = _style.value?.outfits ?: return@launch
                    val updatedOutfits = currentOutfits.map { outfit ->
                        if (outfit.id == outfitId) {
                            outfit.copy(likes = outfit.likes + 1)
                        } else {
                            outfit
                        }
                    }
                    _style.value = _style.value?.copy(outfits = updatedOutfits)

                    // 3. Tell the backend to increment the like
                    outfitRepository.likeOutfit(outfitId)
                }
            } catch (_: Exception) {
                // Silent fail
            }
        }
    }

    fun likeOutfit(outfitId: String) {
        viewModelScope.launch {
            try {
                outfitRepository.likeOutfit(outfitId)
                loadStyle()
            } catch (_: Exception) {
            }
        }
    }
}