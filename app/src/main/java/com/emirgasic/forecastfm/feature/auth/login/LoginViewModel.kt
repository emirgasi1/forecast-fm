package com.emirgasic.forecastfm.feature.auth.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()


    fun updateEmail(value: String) {
        _email.value = value
    }


    fun updatePassword(value: String) {
        _password.value = value
    }


    fun login() {
        // Authentication will be added later
    }

}