package com.emirgasic.forecastfm.feature.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.SavedOutfitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SavedStylesViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val savedOutfitRepository = SavedOutfitRepository()

    private val _savedStyles = MutableStateFlow<List<Outfit>>(emptyList())
    val savedStyles: StateFlow<List<Outfit>> = _savedStyles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadSavedStyles()
    }

    fun loadSavedStyles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    val responses = savedOutfitRepository.getSavedOutfits(userId)
                    _savedStyles.value = responses.map { response ->
                        Outfit(
                            id = response.id,
                            imageUrl = "${com.emirgasic.forecastfm.network.ApiClient.baseUrl()}${response.imageUrl}",
                            title = response.title,
                            weatherCondition = response.weatherCondition,
                            season = response.season,
                            likes = response.likes
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _isLoading.value = false
        }
    }

    fun unsaveStyle(outfitId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    savedOutfitRepository.unsaveOutfit(outfitId, userId)
                    _savedStyles.value = _savedStyles.value.filter { it.id != outfitId }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}