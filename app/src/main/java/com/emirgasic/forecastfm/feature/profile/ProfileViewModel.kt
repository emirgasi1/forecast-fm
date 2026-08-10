package com.emirgasic.forecastfm.feature.profile

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.data.model.Profile
import com.emirgasic.forecastfm.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        _profile.value = repository.getProfile()
    }
}