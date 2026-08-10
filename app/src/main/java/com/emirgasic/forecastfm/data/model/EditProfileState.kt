package com.emirgasic.forecastfm.data.model

data class EditProfileState(

    val username: String,

    val bio: String,

    val profileImage: Int,

    val favoriteLocation: String,

    val locations: List<String>

)