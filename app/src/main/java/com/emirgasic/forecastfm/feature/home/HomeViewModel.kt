package com.emirgasic.forecastfm.feature.home

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.data.model.Home
import com.emirgasic.forecastfm.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val repository = HomeRepository()

    private val _home = MutableStateFlow<Home?>(null)

    val home: StateFlow<Home?> =
        _home.asStateFlow()


    init {
        loadHome()
    }


    private fun loadHome() {

        _home.value = repository.getHome()

    }

}