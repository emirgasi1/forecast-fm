package com.emirgasic.forecastfm.core.navigation

import androidx.compose.ui.Modifier

object Routes{


    const val Splash="splash"
    const val Welcome="welcome"
    const val Login="login"
    const val Register="register"
    const val ForgotPassword="forgotpassword"
    const val Home="home"
    const val Profile="profile"
    const val Settings="settings"

    const val EditProfile="editprofile"

    const val Music="music"

    const val Playlist = "playlist/{playlistId}"

    fun playlistRoute(id:String):String {
        return "playlist/$id"
    }    const val MusicHistory="musichistory"

    const val LocationDetails = "locationDetails/{location}"
    fun locationDetailsRoute(location: String): String {

        return "locationDetails/$location"

    }
    const val PlaceRecommendation = "place_recommendation"
    const val Feed="feed"

    const val Weather="weather"
    const val Map="map"
    const val Style="style"

    const val NewPost="newpost"
    const val Main="main"
    const val Comments = "comments/{postId}"

    fun commentsRoute(postId: String): String {
        return "comments/$postId"
    }
}