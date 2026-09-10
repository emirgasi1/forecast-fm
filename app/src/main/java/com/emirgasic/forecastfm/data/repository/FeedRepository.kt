package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.FeedPost
import com.emirgasic.forecastfm.data.model.User
import com.emirgasic.forecastfm.data.model.Weather
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.location.LocationApi
import com.emirgasic.forecastfm.network.weather.WeatherApi

class FeedRepository(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val locationRepository: LocationRepository = LocationRepository(),
    private val playlistRepository: PlaylistRepository,
    private val commentRepository: CommentRepository,
    private val weatherApi: WeatherApi = WeatherApi()
) {

    suspend fun getPosts(userId: String): List<FeedPost> {
        val userResponse = userRepository.getUser(userId)

        if (userResponse == null) {
            return emptyList()
        }

        val user = User(
            id = userResponse.id,
            username = userResponse.username,
            bio = userResponse.bio ?: "",
            profileImage = R.drawable.outfit3,
            favoriteLocation = userResponse.favoriteLocation ?: "",
            likes = 0,
            posts = 0,
            saved = 0
        )

        val postResponses = postRepository.getPosts()

        val locations = locationRepository.getLocations()
        val location = locations.firstOrNull()
        val allPlaylists = playlistRepository.getPlaylists()

        return postResponses.map { post ->
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

            val commentCount = try {
                commentRepository.getComments(post.id).size
            } catch (e: Exception) {
                0
            }

            FeedPost(
                id = post.id,
                user = user,
                image = if (!post.imageUrl.isNullOrBlank()) {
                    "${ApiClient.baseUrl()}${post.imageUrl}"
                } else {
                    "https://picsum.photos/seed/${post.id}/400/400"
                },
                caption = post.caption ?: "",
                weather = weather,
                playlist = matchingPlaylist,
                time = post.createdAt,
                likes = 0,
                comments = commentCount
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
}