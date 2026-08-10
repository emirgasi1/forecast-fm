package com.emirgasic.forecastfm.feature.style

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.data.model.Style
import com.emirgasic.forecastfm.data.repository.StyleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StyleViewModel : ViewModel() {

    private val repository = StyleRepository()

    private val _style = MutableStateFlow<Style?>(null)
    val style: StateFlow<Style?> = _style.asStateFlow()

    init {
        loadStyle()
    }

    private fun loadStyle() {
        _style.value = repository.getStyle()
    }

}