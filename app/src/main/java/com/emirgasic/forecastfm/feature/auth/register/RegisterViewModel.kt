package com.emirgasic.forecastfm.feature.auth.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _passwordCheck = MutableStateFlow("")
    val passwordCheck: StateFlow<String> = _passwordCheck.asStateFlow()

    private val _checkMark = MutableStateFlow(false)
    val checkMark: StateFlow<Boolean> = _checkMark.asStateFlow()


    fun updateUsername(value: String) {
        _username.value = value
    }

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun updatePasswordCheck(value: String) {
        _passwordCheck.value = value
    }

    fun updateCheckMark(value: Boolean) {
        _checkMark.value = value
    }


    fun register() {
        // Registration will be added later
    }

}