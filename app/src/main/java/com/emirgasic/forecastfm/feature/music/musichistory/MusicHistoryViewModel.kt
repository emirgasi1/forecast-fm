package com.emirgasic.forecastfm.feature.music.musichistory

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.data.model.MusicHistory
import com.emirgasic.forecastfm.data.repository.MusicHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicHistoryViewModel : ViewModel() {

    private val repository = MusicHistoryRepository()

    private val _history =
        MutableStateFlow<List<MusicHistory>>(emptyList())

    val history: StateFlow<List<MusicHistory>> =
        _history.asStateFlow()

    init {
        _history.value = repository.getMusicHistory()
    }
}