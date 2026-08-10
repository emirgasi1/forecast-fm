package com.emirgasic.forecastfm.data.model

data class NewPost(

    val image: String?,

    val caption: String,

    val weather: String,

    val location: String,

    val selectedPlaylist: String,

    val playlists: List<String>

)