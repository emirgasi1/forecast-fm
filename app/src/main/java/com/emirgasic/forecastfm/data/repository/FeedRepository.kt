package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.utils.resolveImageUrlOr
import com.emirgasic.forecastfm.data.model.FeedPost
import com.emirgasic.forecastfm.data.model.User
import com.emirgasic.forecastfm.data.model.Weather
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.weather.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedRepository(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val locationRepository: LocationRepository = LocationRepository(),
    private val playlistRepository: PlaylistRepository,
    private val commentRepository: CommentRepository,
    private val weatherApi: WeatherApi = WeatherApi()
) {

    suspend fun getPosts(userId: String): List<FeedPost> = withContext(Dispatchers.IO) {
        val userResponse = userRepository.getUser(userId)
            ?: return@withContext emptyList()

        val user = User(
            id = userResponse.id,
            username = userResponse.username,
            bio = userResponse.bio ?: "",
            profileImage = resolveImageUrlOr(
                userResponse.profileImageUrl,
                "https://picsum.photos/seed/${userResponse.username}/200/200"
            ),
            favoriteLocation = userResponse.favoriteLocation ?: "",
            likes = 0,
            posts = 0,
            saved = 0
        )

        val postResponses = postRepository.getPosts()
        val locations = locationRepository.getLocations()
        val location = locations.firstOrNull()
        val allPlaylists = playlistRepository.getPlaylists()

        // Fetch weather ONCE for the feed, not per post.
        val weather = if (location != null) {
            try {
                val weatherData = weatherApi.getWeather(
                    location = location.name,
                    latitude = location.latitude,
                    longitude = location.longitude
                )
                Weather(
                    location = weatherData.location,
                    temperature = weatherData.temperature,
                    condition = weatherData.condition,
                    feelsLike = weatherData.feelsLike,
                    humidity = weatherData.humidity,
                    wind = weatherData.wind,
                    uvIndex = weatherData.uvIndex,
                    airQuality = weatherData.airQuality,
                    icon = getWeatherIcon(weatherData.condition)
                )
            } catch (e: Exception) {
                getDefaultWeather()
            }
        } else {
            getDefaultWeather()
        }

        val matchingPlaylist = allPlaylists.find {
            it.weather.equals(weather.condition, ignoreCase = true)
        } ?: allPlaylists.firstOrNull()

        // No more per-post API calls. Likes and comment counts come from the payload.
        postResponses.map { post ->
            FeedPost(
                id = post.id,
                user = user,
                image = resolveImageUrlOr(
                    post.imageUrl,
                    "https://picsum.photos/seed/${post.id}/400/400"
                ),
                caption = post.caption ?: "",
                weather = weather,
                playlist = matchingPlaylist,
                outfitId = post.outfitId,
                outfitTitle = post.outfitTitle,
                time = formatDate(post.createdAt),
                likes = post.likes ?: 0,
                comments = post.commentCount ?: 0
            )
        }
    }

    private fun getDefaultWeather(): Weather {
        return Weather(
            location = "Sarajevo",
            temperature = "22°C",
            condition = "Sunny",
            feelsLike = "24°C",
            humidity = "55%",
            wind = "8 km/h",
            uvIndex = "5UV",
            airQuality = "Good",
            icon = R.drawable.sun
        )
    }

    private fun getWeatherIcon(condition: String): Int {
        return when (condition.lowercase()) {
            "sunny", "clear" -> R.drawable.sun
            "clouds", "cloudy", "partly cloudy" -> R.drawable.sunny_cloudy
            "rain", "drizzle", "heavy rain" -> R.drawable.heavy_rain
            "snow" -> R.drawable.snow
            else -> R.drawable.sun
        }
    }

    private fun formatDate(iso: String): String {
        return try {
            val instant = java.time.Instant.parse(iso)
            java.time.format.DateTimeFormatter
                .ofPattern("d MMM yyyy")
                .withZone(java.time.ZoneId.systemDefault())
                .format(instant)
        } catch (e: Exception) {
            iso
        }
    }
}