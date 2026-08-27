package com.emirgasic.forecastfm.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.Profile
import com.emirgasic.forecastfm.data.repository.ProfileRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.network.post.PostApi
import com.emirgasic.forecastfm.network.profile.ProfileApi
import com.emirgasic.forecastfm.network.user.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val profileApi = ProfileApi()

    private val repository =
        ProfileRepository(
            profileApi = profileApi
        )

    private val _uiState =
        MutableStateFlow<ProfileUiState>(
            ProfileUiState.Loading
        )

    val uiState: StateFlow<ProfileUiState> =
        _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {

        viewModelScope.launch {

            _uiState.value =
                ProfileUiState.Loading

            try {

                val profile =
                    repository.getProfile()

                _uiState.value =
                    ProfileUiState.Success(profile)

            } catch (e: Exception) {

                _uiState.value =
                    ProfileUiState.Error(
                        e.message
                            ?: "Failed to load profile"
                    )
            }
        }
    }
}