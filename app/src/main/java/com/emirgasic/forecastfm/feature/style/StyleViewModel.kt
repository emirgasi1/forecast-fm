package com.emirgasic.forecastfm.feature.style

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.Style
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.SavedOutfitRepository
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StyleViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val outfitRepository = OutfitRepository()
    private val savedOutfitRepository = SavedOutfitRepository()
    private val weatherRepository = WeatherRepository()

    private val _style = MutableStateFlow<Style?>(null)
    val style: StateFlow<Style?> = _style.asStateFlow()

    private val _savedOutfitIds = MutableStateFlow<Set<String>>(emptySet())
    val savedOutfitIds: StateFlow<Set<String>> = _savedOutfitIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadStyle()
        loadSavedOutfits()
    }

    fun loadStyle() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val outfits = outfitRepository.getTrendingOutfits()
                _style.value = Style(outfits = outfits)
            } catch (e: Exception) {
                _error.value = e.message
            }
            _isLoading.value = false
        }
    }

    private fun loadSavedOutfits() {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    val savedOutfits = savedOutfitRepository.getSavedOutfits(userId)
                    _savedOutfitIds.value = savedOutfits.map { it.id }.toSet()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleSaveOutfit(outfitId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first() ?: return@launch

                val isSaved = _savedOutfitIds.value.contains(outfitId)

                if (isSaved) {
                    savedOutfitRepository.unsaveOutfit(outfitId, userId)
                    _savedOutfitIds.value = _savedOutfitIds.value - outfitId
                } else {
                    savedOutfitRepository.saveOutfit(outfitId, userId)
                    _savedOutfitIds.value = _savedOutfitIds.value + outfitId
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun likeOutfit(outfitId: String) {
        viewModelScope.launch {
            try {
                outfitRepository.likeOutfit(outfitId)
                loadStyle()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}