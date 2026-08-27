package com.emirgasic.forecastfm.feature.feed

import com.emirgasic.forecastfm.data.model.FeedPost

sealed interface FeedUiState {

    data object Loading : FeedUiState

    data class Success(
        val posts: List<FeedPost>
    ) : FeedUiState

    data class Error(
        val message: String
    ) : FeedUiState
}