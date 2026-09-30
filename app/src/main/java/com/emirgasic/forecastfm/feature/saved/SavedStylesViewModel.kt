package com.emirgasic.forecastfm.feature.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.repository.SavedOutfitRepository
import com.emirgasic.forecastfm.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SavedStylesViewModel(
    private val tokenManager: TokenManager,
    private val savedOutfitRepository: SavedOutfitRepository
) : ViewModel() {

    private val _savedStyles = MutableStateFlow<List<Outfit>>(emptyList())
    val savedStyles: StateFlow<List<Outfit>> = _savedStyles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadSavedStyles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    val responses = savedOutfitRepository.getSavedOutfits(userId)
                    _savedStyles.value = responses.map { response ->
                        val fullImageUrl = if (
                            response.imageUrl.startsWith("http://") ||
                            response.imageUrl.startsWith("https://")
                        ) {
                            response.imageUrl
                        } else {
                            "${ApiClient.baseUrl()}${response.imageUrl}"
                        }

                        Outfit(
                            id = response.id,
                            imageUrl = fullImageUrl,
                            title = response.title,
                            weatherCondition = response.weatherCondition,
                            season = response.season,
                            likes = response.likes,
                            storeName = response.storeName,
                            storeAddress = response.storeAddress,
                            price = response.price,
                            storePhone = response.storePhone,
                            productUrl = response.productUrl
                        )
                    }
                }
            } catch (_: Exception) {
                // Silent failure
            }
            _isLoading.value = false
        }
    }

    fun unsaveStyle(outfitId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first() ?: return@launch

                savedOutfitRepository.unsaveOutfit(outfitId, userId)
                _savedStyles.value = _savedStyles.value.filter { it.id != outfitId }
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }
}