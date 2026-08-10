package com.emirgasic.forecastfm.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _navigateToLogin = MutableSharedFlow<Unit>()

    val navigateToLogin = _navigateToLogin.asSharedFlow()


    init {
        startSplash()
    }


    private fun startSplash() {

        viewModelScope.launch {

            delay(2000)

            _navigateToLogin.emit(Unit)

        }

    }

}