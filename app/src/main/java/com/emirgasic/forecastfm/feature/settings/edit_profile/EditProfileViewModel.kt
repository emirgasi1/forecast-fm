package com.emirgasic.forecastfm.feature.settings.edit_profile

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.EditProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditProfileViewModel : ViewModel() {


    private val _profile =
        MutableStateFlow(

            EditProfileState(

                username = "",

                bio = "",

                profileImage = R.drawable.profile_picture,

                favoriteLocation = "",

                locations = listOf(
                    "Baščaršija",
                    "Ilidža",
                    "Trebević",
                    "Dobrinja"
                )

            )

        )


    val profile: StateFlow<EditProfileState> =
        _profile.asStateFlow()



    fun updateUsername(value: String){

        _profile.value = _profile.value.copy(
            username = value
        )

    }



    fun updateBio(value: String){

        _profile.value = _profile.value.copy(
            bio = value
        )

    }



    fun updateLocation(value: String){

        _profile.value = _profile.value.copy(
            favoriteLocation = value
        )

    }



    fun updateImage(image: Int){

        _profile.value = _profile.value.copy(
            profileImage = image
        )

    }


}