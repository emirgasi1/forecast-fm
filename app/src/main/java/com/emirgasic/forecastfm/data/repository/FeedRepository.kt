package com.emirgasic.forecastfm.data.repository


import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Comment
import com.emirgasic.forecastfm.data.model.FeedPost
import com.emirgasic.forecastfm.data.model.Music
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.model.User
import com.emirgasic.forecastfm.data.model.Weather


class FeedRepository(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) {

    private val weather = Weather(
        location = "Baščaršija",
        temperature = "22°C",
        condition = "Sunny",
        feelsLike = "24°C",
        humidity = "55%",
        wind = "8 km/h",
        uvIndex = "20UV",
        airQuality = "Good",
        icon = R.drawable.sun
    )

    private val playlist = Playlist(
        id = "1",
        title = "Morning Coffee",
        genre = "Jazz",
        mood = "Relax",
        albumImageUrl = null,
        weather = "Sunny",
        temperature = "22°C",
        location = "Baščaršija",
        songs = listOf(
            Music(
                id = "1",
                title = "Coffee Time",
                artist = "Sarajevo Jazz",
                duration = "3:45",
                albumImageUrl = null
            ),
            Music(
                id = "2",
                title = "Morning Walk",
                artist = "City Lights",
                duration = "4:10",
                albumImageUrl = null
            )
        ),
        likes = 240,
        spotifyUrl = "https://open.spotify.com/",
        youtubeUrl = "https://www.youtube.com/"
    )
    suspend fun getPosts(): List<FeedPost> {


        val userResponse = userRepository.getCurrentUser()

        if(userResponse==null){
            return emptyList()
        }

        val user = User(
            id = userResponse.id,
            username = userResponse.username,
            bio = userResponse.bio ?: "",
            profileImage = R.drawable.outfit3,
            favoriteLocation = userResponse.favoriteLocation ?: "",
            likes = 248,
            posts = 32,
            saved = 14
        )

        val postResponses=postRepository.getPosts()


        return postResponses.map{ post->

            FeedPost(
                id = post.id,
                user = user,
                image = R.drawable.outfit1,
                caption = post.caption ?: "",
                weather = weather,
                playlist = playlist,
                time = post.createdAt,
                likes = 128,
                comments = 24
            )
        }
    }

    fun getComments(postId: String): List<Comment> {
        return emptyList()
    }
}