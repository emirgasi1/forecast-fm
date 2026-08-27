package com.emirgasic.forecastfm.feature.home

import com.emirgasic.forecastfm.data.model.Home

sealed interface HomeUiState {

    data object Loading : HomeUiState

    data class Success(
        val home: Home
    ) : HomeUiState

    data class Error(
        val message: String
    ) : HomeUiState
}