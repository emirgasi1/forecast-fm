package com.emirgasic.forecastfm.feature.profile

import com.emirgasic.forecastfm.data.model.Profile

sealed interface ProfileUiState{
    data object Loading : ProfileUiState

    data class Success(
        val profile:Profile
    ): ProfileUiState

    data class Error(
        val message: String
    ): ProfileUiState
}