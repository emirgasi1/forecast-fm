package com.emirgasic.forecastfm.data.recommender

import com.emirgasic.forecastfm.data.model.Playlist
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendationScorerTest {


    @Test
    fun `scorePlaylist returns 0 for empty user prefs and 0 likes`() {
        val score = RecommendationScorer.scorePlaylist(
            playlist = playlist(likes = 0),
            userGenres = emptySet(),
            userMoods = emptySet(),
            userWeather = emptySet(),
            currentWeather = "Snowy"
        )
        assertEquals(0, score)
    }

    @Test
    fun `scorePlaylist adds 10 for genre match`() {
        val score = RecommendationScorer.scorePlaylist(
            playlist = playlist(genre = "Rock", likes = 0),
            userGenres = setOf("Rock"),
            userMoods = emptySet(),
            userWeather = emptySet(),
            currentWeather = "Snowy"
        )
        assertEquals(10, score)
    }

    @Test
    fun `scorePlaylist adds 6 for mood match`() {
        val score = RecommendationScorer.scorePlaylist(
            playlist = playlist(mood = "Chill", likes = 0),
            userGenres = emptySet(),
            userMoods = setOf("Chill"),
            userWeather = emptySet(),
            currentWeather = "Snowy"
        )
        assertEquals(6, score)
    }

    @Test
    fun `scorePlaylist adds 8 for weather pref match`() {
        val score = RecommendationScorer.scorePlaylist(
            playlist = playlist(weather = "Rainy", likes = 0),
            userGenres = emptySet(),
            userMoods = emptySet(),
            userWeather = setOf("Sunny & clear"),
            currentWeather = "Sunny"
        )
        assertEquals(8, score)
    }

    @Test
    fun `scorePlaylist adds 4 when playlist weather matches current and no user pref match`() {
        val score = RecommendationScorer.scorePlaylist(
            playlist = playlist(weather = "Sunny", likes = 0),
            userGenres = emptySet(),
            userMoods = emptySet(),
            userWeather = setOf("Rainy"),
            currentWeather = "Sunny"
        )
        assertEquals(4, score)
    }

    @Test
    fun `scorePlaylist adds likes divided by 20`() {
        val score = RecommendationScorer.scorePlaylist(
            playlist = playlist(likes = 100),
            userGenres = emptySet(),
            userMoods = emptySet(),
            userWeather = emptySet(),
            currentWeather = "Snowy"
        )
        assertEquals(5, score)
    }

    @Test
    fun `scorePlaylist sums all bonuses`() {
        val score = RecommendationScorer.scorePlaylist(
            playlist = playlist(genre = "Rock", mood = "Chill", weather = "Sunny", likes = 100),
            userGenres = setOf("Rock"),
            userMoods = setOf("Chill"),
            userWeather = setOf("Sunny & clear"),
            currentWeather = "Sunny"
        )
        assertEquals(29, score)
    }


    @Test
    fun `rankPlaylists returns empty for empty input`() {
        val ranked = RecommendationScorer.rankPlaylists(
            allPlaylists = emptyList(),
            userGenres = emptySet(),
            userMoods = emptySet(),
            userWeather = emptySet(),
            currentWeather = "Snowy",
            take = 3
        )
        assertTrue(ranked.isEmpty())
    }

    @Test
    fun `rankPlaylists respects take parameter`() {
        val playlists = List(10) { playlist(id = "p$it", likes = 100 - it) }
        val ranked = RecommendationScorer.rankPlaylists(
            allPlaylists = playlists,
            userGenres = emptySet(),
            userMoods = emptySet(),
            userWeather = emptySet(),
            currentWeather = "Snowy",
            take = 4
        )
        assertEquals(4, ranked.size)
    }

    @Test
    fun `rankPlaylists sorts by score descending then likes descending`() {
        val playlists = listOf(
            playlist(id = "low", likes = 20),       // score 1
            playlist(id = "high1", likes = 100),    // score 5
            playlist(id = "high2", likes = 200)     // score 10
        )
        val ranked = RecommendationScorer.rankPlaylists(
            allPlaylists = playlists,
            userGenres = emptySet(),
            userMoods = emptySet(),
            userWeather = emptySet(),
            currentWeather = "Snowy",
            take = 3
        )
        assertEquals(listOf("high2", "high1", "low"), ranked.map { it.id })
    }


    @Test
    fun `genreMatches direct contains`() {
        assertTrue(RecommendationScorer.genreMatches("rock", "Classic Rock"))
        assertTrue(RecommendationScorer.genreMatches("Rock", "rock"))
    }

    @Test
    fun `genreMatches composite user picks`() {
        assertTrue(RecommendationScorer.genreMatches("indie / alternative", "Indie Pop"))
        assertTrue(RecommendationScorer.genreMatches("r&b / soul", "Soul"))
        assertTrue(RecommendationScorer.genreMatches("hip-hop", "Hip Hop"))
    }

    @Test
    fun `genreMatches unrelated returns false`() {
        assertFalse(RecommendationScorer.genreMatches("Rock", "Jazz"))
    }


    @Test
    fun `moodMatches composite user picks`() {
        assertTrue(RecommendationScorer.moodMatches("chill & cozy", "cozy"))
        assertTrue(RecommendationScorer.moodMatches("energetic & social", "party"))
        assertTrue(RecommendationScorer.moodMatches("romantic", "date night"))
    }

    @Test
    fun `moodMatches unrelated returns false`() {
        assertFalse(RecommendationScorer.moodMatches("chill", "energetic"))
    }


    @Test
    fun `weatherMatches composite user picks`() {
        assertTrue(RecommendationScorer.weatherMatches("sunny & clear", "Clear sky"))
        assertTrue(RecommendationScorer.weatherMatches("rainy", "Drizzle"))
        assertTrue(RecommendationScorer.weatherMatches("snowy", "Light snow"))
    }

    @Test
    fun `weatherMatches unrelated returns false`() {
        assertFalse(RecommendationScorer.weatherMatches("sunny & clear", "Rainy"))
    }


    @Test
    fun `weatherCategoryMatches sunny matches sunny and clear`() {
        assertTrue(RecommendationScorer.weatherCategoryMatches("Sunny", "Sunny"))
        assertTrue(RecommendationScorer.weatherCategoryMatches("Sunny", "Clear"))
    }

    @Test
    fun `weatherCategoryMatches clear matches sunny and clear`() {
        assertTrue(RecommendationScorer.weatherCategoryMatches("Clear", "Sunny"))
        assertTrue(RecommendationScorer.weatherCategoryMatches("Clear", "Clear"))
    }

    @Test
    fun `weatherCategoryMatches cloudy matches cloudy and partly cloudy`() {
        assertTrue(RecommendationScorer.weatherCategoryMatches("Cloudy", "Cloudy"))
        assertTrue(RecommendationScorer.weatherCategoryMatches("Partly cloudy", "Cloudy"))
        assertTrue(RecommendationScorer.weatherCategoryMatches("Overcast", "Partly cloudy"))
    }

    @Test
    fun `weatherCategoryMatches rain matches rain and rainy`() {
        assertTrue(RecommendationScorer.weatherCategoryMatches("Rain", "Rain"))
        assertTrue(RecommendationScorer.weatherCategoryMatches("Drizzle", "Rainy"))
        assertTrue(RecommendationScorer.weatherCategoryMatches("Heavy rain", "Rain"))
    }

    @Test
    fun `weatherCategoryMatches unknown condition returns false`() {
        assertFalse(RecommendationScorer.weatherCategoryMatches("Snow", "Sunny"))
        assertFalse(RecommendationScorer.weatherCategoryMatches("Foggy", "Clear"))
    }

    @Test
    fun `weatherCategoryMatches sunny does not match rainy`() {
        assertFalse(RecommendationScorer.weatherCategoryMatches("Sunny", "Rain"))
        assertFalse(RecommendationScorer.weatherCategoryMatches("Sunny", "Cloudy"))
    }


    private fun playlist(
        id: String = "id",
        genre: String = "Genre",
        mood: String = "Mood",
        weather: String = "Weather",
        likes: Int = 0
    ) = Playlist(
        id = id,
        title = "Title",
        genre = genre,
        mood = mood,
        albumImageUrl = null,
        weather = weather,
        temperature = "20",
        location = "Sarajevo",
        songs = emptyList(),
        likes = likes,
        spotifyUrl = null,
        youtubeUrl = null,
        bestFor = emptyList()
    )
}