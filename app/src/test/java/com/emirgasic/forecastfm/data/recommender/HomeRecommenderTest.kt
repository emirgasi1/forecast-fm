package com.emirgasic.forecastfm.data.recommender

import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.network.location.LocationResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeRecommenderTest {


    @Test
    fun `haversine is zero for identical points`() {
        val d = HomeRecommender.haversineKm(43.8563, 18.4131, 43.8563, 18.4131)
        assertEquals(0.0, d, 0.0001)
    }

    @Test
    fun `haversine between Sarajevo and Mostar is roughly 70 km`() {
        // Sarajevo: 43.8563, 18.4131; Mostar: 43.3438, 17.8078
        val d = HomeRecommender.haversineKm(43.8563, 18.4131, 43.3438, 17.8078)
        assertTrue("Expected ~70km, got $d", d in 65.0..80.0)
    }

    @Test
    fun `haversine is symmetric`() {
        val a = HomeRecommender.haversineKm(43.8563, 18.4131, 43.3438, 17.8078)
        val b = HomeRecommender.haversineKm(43.3438, 17.8078, 43.8563, 18.4131)
        assertEquals(a, b, 0.0001)
    }


    @Test
    fun `pickNearestLocation returns first when lat lng are null`() {
        val locations = listOf(
            location("a", "A", 43.85, 18.41),
            location("b", "B", 44.00, 19.00)
        )
        val picked = HomeRecommender.pickNearestLocation(locations, null, null)
        assertEquals("a", picked.id)
    }

    @Test
    fun `pickNearestLocation returns nearest to user coords`() {
        val locations = listOf(
            location("far", "Far", 44.80, 20.50),
            location("near", "Near", 43.86, 18.41),
            location("medium", "Medium", 44.00, 19.00)
        )
        val picked = HomeRecommender.pickNearestLocation(locations, 43.8563, 18.4131)
        assertEquals("near", picked.id)
    }

    @Test
    fun `pickNearestLocation returns single location`() {
        val only = listOf(location("only", "Only", 43.85, 18.41))
        val picked = HomeRecommender.pickNearestLocation(only, 40.0, 20.0)
        assertEquals("only", picked.id)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `pickNearestLocation throws on empty list`() {
        HomeRecommender.pickNearestLocation(emptyList(), 43.0, 18.0)
    }


    @Test
    fun `scorePlaylist returns just likes score when no user prefs match`() {
        val playlist = playlist(genre = "Rock", mood = "Chill", weather = "Rainy", likes = 100)
        val score = HomeRecommender.scorePlaylist(
            playlist = playlist,
            userGenres = emptySet(),
            userMoods = emptySet(),
            userWeather = emptySet(),
            currentWeather = "Sunny"
        )
        assertEquals(5, score)
    }

    @Test
    fun `scorePlaylist adds genre match points`() {
        val playlist = playlist(genre = "Rock", likes = 0)
        val score = HomeRecommender.scorePlaylist(
            playlist = playlist,
            userGenres = setOf("Rock"),
            userMoods = emptySet(),
            userWeather = emptySet(),
            currentWeather = "Snowy"
        )
        assertEquals(10, score)
    }

    @Test
    fun `scorePlaylist adds mood match points`() {
        val playlist = playlist(mood = "Chill", likes = 0)
        val score = HomeRecommender.scorePlaylist(
            playlist = playlist,
            userGenres = emptySet(),
            userMoods = setOf("Chill"),
            userWeather = emptySet(),
            currentWeather = "Snowy"
        )
        assertEquals(6, score)
    }

    @Test
    fun `scorePlaylist adds weather pref points when user pref matches current weather`() {
        val playlist = playlist(weather = "Rainy", likes = 0)
        val score = HomeRecommender.scorePlaylist(
            playlist = playlist,
            userGenres = emptySet(),
            userMoods = emptySet(),
            userWeather = setOf("Sunny & clear"),
            currentWeather = "Sunny"
        )
        assertEquals(8, score)
    }

    @Test
    fun `scorePlaylist adds playlist weather match when no pref match`() {
        val playlist = playlist(weather = "Sunny", likes = 0)
        val score = HomeRecommender.scorePlaylist(
            playlist = playlist,
            userGenres = emptySet(),
            userMoods = emptySet(),
            userWeather = setOf("Rainy"),
            currentWeather = "Sunny"
        )
        assertEquals(4, score)
    }

    @Test
    fun `scorePlaylist sums all matches`() {
        val playlist = playlist(genre = "Rock", mood = "Chill", weather = "Sunny", likes = 100)
        val score = HomeRecommender.scorePlaylist(
            playlist = playlist,
            userGenres = setOf("Rock"),
            userMoods = setOf("Chill"),
            userWeather = setOf("Sunny & clear"),
            currentWeather = "Sunny"
        )
        assertEquals(29, score)
    }


    @Test
    fun `rankPlaylists returns top 3`() {
        val playlists = listOf(
            playlist("a", likes = 100),
            playlist("b", likes = 80),
            playlist("c", likes = 60),
            playlist("d", likes = 40),
            playlist("e", likes = 20)
        )
        val ranked = HomeRecommender.rankPlaylists(
            allPlaylists = playlists,
            userGenres = emptySet(),
            userMoods = emptySet(),
            userWeather = emptySet(),
            currentWeather = "Sunny"
        )
        assertEquals(3, ranked.size)
        assertEquals("a", ranked[0].id)
        assertEquals("b", ranked[1].id)
        assertEquals("c", ranked[2].id)
    }

    @Test
    fun `rankPlaylists puts genre-matching playlist ahead of non-matching with lower likes`() {
        val popular = playlist("popular", genre = "Pop", likes = 60)
        val matched = playlist("matched", genre = "Rock", likes = 10)

        val ps = HomeRecommender.scorePlaylist(
            playlist = popular,
            userGenres = setOf("Rock"),
            userMoods = emptySet(),
            userWeather = emptySet(),
            currentWeather = "Snowy"
        )
        val ms = HomeRecommender.scorePlaylist(
            playlist = matched,
            userGenres = setOf("Rock"),
            userMoods = emptySet(),
            userWeather = emptySet(),
            currentWeather = "Snowy"
        )
        println("DEBUG popular score = $ps")
        println("DEBUG matched score = $ms")

        // Print the actual ranking
        val ranked = HomeRecommender.rankPlaylists(
            allPlaylists = listOf(popular, matched),
            userGenres = setOf("Rock"),
            userMoods = emptySet(),
            userWeather = emptySet(),
            currentWeather = "Snowy"
        )
        println("DEBUG ranking = ${ranked.map { it.id }}")

        assertEquals("matched", ranked[0].id)
    }
    @Test
    fun `rankPlaylists returns empty list for empty input`() {
        val ranked = HomeRecommender.rankPlaylists(
            allPlaylists = emptyList(),
            userGenres = emptySet(),
            userMoods = emptySet(),
            userWeather = emptySet(),
            currentWeather = "Sunny"
        )
        assertTrue(ranked.isEmpty())
    }


    @Test
    fun `genreMatches direct contains`() {
        assertTrue(HomeRecommender.genreMatches("rock", "Classic Rock"))
        assertTrue(HomeRecommender.genreMatches("Rock", "rock"))
    }

    @Test
    fun `genreMatches handles composite user picks`() {
        assertTrue(HomeRecommender.genreMatches("indie / alternative", "Indie Pop"))
        assertTrue(HomeRecommender.genreMatches("r&b / soul", "Soul"))
        assertTrue(HomeRecommender.genreMatches("hip-hop", "Hip Hop"))
        assertTrue(HomeRecommender.genreMatches("electronic", "EDM"))
    }

    @Test
    fun `genreMatches returns false for unrelated`() {
        assertFalse(HomeRecommender.genreMatches("Rock", "Jazz"))
    }


    @Test
    fun `moodMatches direct contains`() {
        assertTrue(HomeRecommender.moodMatches("chill", "Chill Vibes"))
    }

    @Test
    fun `moodMatches handles composite user picks`() {
        assertTrue(HomeRecommender.moodMatches("chill & cozy", "cozy"))
        assertTrue(HomeRecommender.moodMatches("energetic & social", "party"))
        assertTrue(HomeRecommender.moodMatches("romantic", "date night"))
        assertTrue(HomeRecommender.moodMatches("focused & productive", "work"))
    }

    @Test
    fun `moodMatches returns false for unrelated`() {
        assertFalse(HomeRecommender.moodMatches("chill", "energetic"))
    }


    @Test
    fun `weatherMatches direct contains`() {
        assertTrue(HomeRecommender.weatherMatches("rainy", "Light rain"))
    }

    @Test
    fun `weatherMatches handles composite user picks`() {
        assertTrue(HomeRecommender.weatherMatches("sunny & clear", "Clear sky"))
        assertTrue(HomeRecommender.weatherMatches("rainy", "Drizzle"))
        assertTrue(HomeRecommender.weatherMatches("snowy", "Light snow"))
        assertTrue(HomeRecommender.weatherMatches("cool & crisp", "Cold"))
    }

    @Test
    fun `weatherMatches returns false for unrelated`() {
        assertFalse(HomeRecommender.weatherMatches("sunny & clear", "Rainy"))
    }


    private fun location(id: String, name: String, lat: Double, lng: Double) =
        LocationResponse(
            id = id,
            name = name,
            description = "",
            latitude = lat,
            longitude = lng
        )

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