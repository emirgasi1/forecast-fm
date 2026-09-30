package com.emirgasic.forecastfm.data.recommender

import com.emirgasic.forecastfm.data.model.Playlist

object RecommendationScorer {

    private const val SCORE_GENRE_MATCH = 10
    private const val SCORE_MOOD_MATCH = 6
    private const val SCORE_WEATHER_PREF_MATCH = 8
    private const val SCORE_WEATHER_PLAYLIST_MATCH = 4
    private const val LIKES_SCORE_DIVISOR = 20

    fun scorePlaylist(
        playlist: Playlist,
        userGenres: Set<String>,
        userMoods: Set<String>,
        userWeather: Set<String>,
        currentWeather: String
    ): Int {
        var score = 0

        if (userGenres.isNotEmpty() && userGenres.any { genreMatches(it, playlist.genre) }) {
            score += SCORE_GENRE_MATCH
        }

        if (userMoods.isNotEmpty() && userMoods.any { moodMatches(it, playlist.mood) }) {
            score += SCORE_MOOD_MATCH
        }

        if (userWeather.isNotEmpty() && userWeather.any { weatherMatches(it, currentWeather) }) {
            score += SCORE_WEATHER_PREF_MATCH
        } else if (playlist.weather.equals(currentWeather, ignoreCase = true)) {
            score += SCORE_WEATHER_PLAYLIST_MATCH
        }

        score += playlist.likes / LIKES_SCORE_DIVISOR

        return score
    }

    fun rankPlaylists(
        allPlaylists: List<Playlist>,
        userGenres: Set<String>,
        userMoods: Set<String>,
        userWeather: Set<String>,
        currentWeather: String,
        take: Int
    ): List<Playlist> {
        if (allPlaylists.isEmpty()) return emptyList()

        return allPlaylists
            .map { it to scorePlaylist(it, userGenres, userMoods, userWeather, currentWeather) }
            .sortedWith(
                compareByDescending<Pair<Playlist, Int>> { it.second }
                    .thenByDescending { it.first.likes }
            )
            .take(take)
            .map { it.first }
    }

    fun genreMatches(userPick: String, dbGenre: String): Boolean {
        val pick = userPick.lowercase().trim()
        val genre = dbGenre.lowercase().trim()

        if (genre.contains(pick) || pick.contains(genre)) return true

        return when (pick) {
            "indie / alternative" -> genre.contains("indie")
            "r&b / soul" -> genre.contains("r&b") || genre.contains("soul")
            "sevdah / traditional" -> genre.contains("sevdah") || genre.contains("traditional")
            "hip-hop" -> genre.contains("hip-hop") || genre.contains("hip hop")
            "electronic" -> genre.contains("electronic") || genre.contains("edm")
            "jazz / blues" -> genre.contains("jazz") || genre.contains("blues")
            else -> false
        }
    }

    fun moodMatches(userPick: String, dbMood: String): Boolean {
        val pick = userPick.lowercase().trim()
        val mood = dbMood.lowercase().trim()

        if (mood.contains(pick) || pick.contains(mood)) return true

        return when (pick) {
            "chill & cozy" -> mood.contains("chill") || mood.contains("cozy")
            "energetic & social" -> mood.contains("energetic") || mood.contains("party") || mood.contains("social")
            "romantic" -> mood.contains("romantic") || mood.contains("date")
            "focused & productive" -> mood.contains("focus") || mood.contains("work")
            "adventurous" -> mood.contains("adventure") || mood.contains("outdoor")
            "feels good / sunny" -> mood.contains("feel good") || mood.contains("sunny")
            else -> false
        }
    }

    fun weatherMatches(userPick: String, currentWeather: String): Boolean {
        val pick = userPick.lowercase().trim()
        val weather = currentWeather.lowercase().trim()

        if (weather.contains(pick) || pick.contains(weather)) return true

        return when (pick) {
            "sunny & clear" -> weather.contains("sunny") || weather.contains("clear")
            "partly cloudy" -> weather.contains("cloud") || weather.contains("partly")
            "rainy" -> weather.contains("rain") || weather.contains("drizzle")
            "snowy" -> weather.contains("snow")
            "cool & crisp" -> weather.contains("cold") || weather.contains("cool")
            "hot days" -> weather.contains("hot")
            "mild weather" -> weather.contains("mild") || weather.contains("clear")
            else -> false
        }
    }


    fun weatherCategoryMatches(
        currentCondition: String,
        playlistWeather: String
    ): Boolean {
        return when (currentCondition.lowercase().trim()) {
            "sunny", "clear" ->
                playlistWeather.equals("Sunny", ignoreCase = true) ||
                        playlistWeather.equals("Clear", ignoreCase = true)
            "partly cloudy", "cloudy", "overcast" ->
                playlistWeather.equals("Cloudy", ignoreCase = true) ||
                        playlistWeather.equals("Partly cloudy", ignoreCase = true)
            "rain", "drizzle", "light rain", "heavy rain" ->
                playlistWeather.equals("Rain", ignoreCase = true) ||
                        playlistWeather.equals("Rainy", ignoreCase = true)
            else -> false
        }
    }
}