package com.emirgasic.forecastfm.data.recommender

import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.network.location.LocationResponse
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object HomeRecommender {

    private const val EARTH_RADIUS_KM = 6371.0
    private const val TOP_PLAYLIST_COUNT = 3

    fun pickNearestLocation(
        locations: List<LocationResponse>,
        userLat: Double?,
        userLng: Double?
    ): LocationResponse {
        require(locations.isNotEmpty()) { "Locations list must not be empty" }

        if (userLat == null || userLng == null) {
            return locations.first()
        }

        return locations.minByOrNull { loc ->
            haversineKm(userLat, userLng, loc.latitude, loc.longitude)
        } ?: locations.first()
    }

    fun haversineKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).let { it * it } +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).let { it * it }
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    fun rankPlaylists(
        allPlaylists: List<Playlist>,
        userGenres: Set<String>,
        userMoods: Set<String>,
        userWeather: Set<String>,
        currentWeather: String
    ): List<Playlist> {
        return RecommendationScorer.rankPlaylists(
            allPlaylists = allPlaylists,
            userGenres = userGenres,
            userMoods = userMoods,
            userWeather = userWeather,
            currentWeather = currentWeather,
            take = TOP_PLAYLIST_COUNT
        )
    }


    fun scorePlaylist(
        playlist: Playlist,
        userGenres: Set<String>,
        userMoods: Set<String>,
        userWeather: Set<String>,
        currentWeather: String
    ): Int = RecommendationScorer.scorePlaylist(
        playlist, userGenres, userMoods, userWeather, currentWeather
    )

    fun genreMatches(userPick: String, dbGenre: String): Boolean =
        RecommendationScorer.genreMatches(userPick, dbGenre)

    fun moodMatches(userPick: String, dbMood: String): Boolean =
        RecommendationScorer.moodMatches(userPick, dbMood)

    fun weatherMatches(userPick: String, currentWeather: String): Boolean =
        RecommendationScorer.weatherMatches(userPick, currentWeather)
}