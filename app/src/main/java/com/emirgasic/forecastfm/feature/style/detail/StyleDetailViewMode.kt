package com.emirgasic.forecastfm.feature.style.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StyleDetailViewModel : ViewModel() {

    private val outfitRepository = OutfitRepository()

    private val _outfit = MutableStateFlow<Outfit?>(null)
    val outfit: StateFlow<Outfit?> = _outfit.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadOutfit(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = outfitRepository.getOutfitById(id)
                _outfit.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _isLoading.value = false
        }
    }
}