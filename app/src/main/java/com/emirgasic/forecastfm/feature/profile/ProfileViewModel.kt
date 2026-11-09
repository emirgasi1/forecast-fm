package com.emirgasic.forecastfm.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val tokenManager: TokenManager,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            // 1. Try to load from cache first (DataStore)
            val cachedProfile = profileRepository.getLocalProfile()
            if (cachedProfile != null) {
                _uiState.value = ProfileUiState.Success(cachedProfile)
                // Note: We do NOT set _uiState to Loading if we have cached data,
                // so the UI shows the old data instantly while refreshing in the background.
            } else {
                // If there's no cache (first launch), show the loading spinner
                _uiState.value = ProfileUiState.Loading
            }

            // 2. Fetch fresh data from the network
            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                val networkProfile = profileRepository.getProfile(userId)
                _uiState.value = ProfileUiState.Success(networkProfile)

            } catch (e: Exception) {
                // If network fails, but we have a cached profile, just keep showing the cache.
                // Only show an error if the screen is completely empty.
                if (cachedProfile == null) {
                    _uiState.value = ProfileUiState.Error(
                        e.message ?: "Failed to load profile"
                    )
                }
            }
        }
    }
}