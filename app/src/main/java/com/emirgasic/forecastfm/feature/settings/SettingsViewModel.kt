package com.emirgasic.forecastfm.feature.settings

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.core.navigation.Routes

import com.emirgasic.forecastfm.data.model.SettingsOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {


    private val _accountOptions = MutableStateFlow(
        listOf(
            SettingsOption(
                title = "Edit Profile",
                route = Routes.EditProfile
            )
        )
    )

    val accountOptions: StateFlow<List<SettingsOption>> =
        _accountOptions.asStateFlow()



    private val _preferenceOptions = MutableStateFlow(
        listOf(
            SettingsOption(
                title = "Default Location"
            ),
            SettingsOption(
                title = "Theme"
            ),
            SettingsOption(
                title = "Notifications"
            )
        )
    )

    val preferenceOptions: StateFlow<List<SettingsOption>> =
        _preferenceOptions.asStateFlow()



    private val _aboutOptions = MutableStateFlow(
        listOf(
            SettingsOption(
                title = "Privacy Policy"
            ),
            SettingsOption(
                title = "About App"
            )
        )
    )

    val aboutOptions: StateFlow<List<SettingsOption>> =
        _aboutOptions.asStateFlow()

}