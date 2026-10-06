package com.emirgasic.forecastfm.core.navigation

import androidx.compose.ui.Modifier
import android.net.Uri



object Routes {

    const val Splash = "splash"
    const val Welcome = "welcome"
    const val Login = "login"
    const val Register = "register"
    const val ForgotPassword = "forgotpassword"
    const val Home = "home"
    const val Profile = "profile"

    const val PostDetail = "post_detail/{postId}"

    fun postDetailRoute(postId: String): String {
        return "post_detail/${Uri.encode(postId)}"
    }
    const val Settings = "settings"

    const val EditProfile = "editprofile"

    const val Music = "music"

    const val Notifications = "notifications"

    const val DefaultLocation = "default_location"

    const val PrivacyPolicy = "privacy_policy"

    const val Onboarding = "onboarding"

    const val AddOutfit = "add_outfit"
    const val AboutApp = "about_app"

    const val Playlist = "playlist/{playlistId}"

    const val PlaceInfo = "place_info/{placeId}"

    fun placeInfoRoute(placeId: String): String = "place_info/${Uri.encode(placeId)}"

    fun playlistRoute(id: String): String {
        return "playlist/${Uri.encode(id)}"
    }

    const val MusicHistory = "musichistory"

    const val LocationDetails = "locationDetails/{locationId}"

    fun locationDetailsRoute(locationId: String): String {
        return "locationDetails/${Uri.encode(locationId)}"
    }

    const val PlaceRecommendation = "placeRecommendation/{venueId}"

    fun placeRecommendationRoute(venueId: String) = "placeRecommendation/${Uri.encode(venueId)}"

    const val PlaceRecommendationDetail = "place_recommendation_detail/{id}"
    const val PlaceDetail = "place_detail/{id}"
    const val SavedHub = "saved_hub"
    const val SavedPosts = "saved_posts"
    const val SavedPlaylists = "saved_playlists"
    const val SavedStyles = "saved_styles"

    const val StyleDetail = "style_detail/{outfitId}"

    fun styleDetailRoute(id: String): String {
        return "style_detail/${Uri.encode(id)}"
    }

    fun placeRecommendationDetailRoute(id: String): String {
        return "place_recommendation_detail/${Uri.encode(id)}"
    }

    const val Feed = "feed"

    const val Weather = "weather"
    const val Map = "map"
    const val Style = "style"

    const val Admin = "admin"
    const val NewPost = "newpost"
    const val Main = "main"
    const val Comments = "comments/{postId}"
    const val FullMap = "full_map"

    fun commentsRoute(postId: String): String {
        return "comments/${Uri.encode(postId)}"
    }

    const val Route = "route/{destLat}/{destLng}/{originLat}/{originLng}"

    fun routeRoute(
        destLat: Double,
        destLng: Double,
        originLat: Double,
        originLng: Double
    ): String = "route/$destLat/$destLng/$originLat/$originLng"
}