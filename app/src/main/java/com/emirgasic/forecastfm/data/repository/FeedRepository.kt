package com.emirgasic.forecastfm.data.repository


import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Comment
import com.emirgasic.forecastfm.data.model.FeedPost
import com.emirgasic.forecastfm.data.model.Music
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.model.User
import com.emirgasic.forecastfm.data.model.Weather


class FeedRepository {


    private val user = User(
        id = "1",
        username = "Emir",
        bio = "Coffee lover",
        profileImage = R.drawable.outfit3,
        favoriteLocation = "Baščaršija",
        likes = 248,
        posts = 32,
        saved = 14
    )


    private val weather = Weather(
        location = "Baščaršija",
        temperature = "22°C",
        condition = "Sunny",
        feelsLike = "24°C",
        humidity = "55%",
        wind = "8 km/h",
        uvIndex = "20UV",
        airQuality = "Good"
    )


    private val playlist = Playlist(
        id = "1",
        title = "Morning Coffee",
        genre = "Jazz",
        mood = "Relax",
        albumImage = R.drawable.album1,
        weather = "Sunny",
        temperature = "22°C",
        location = "Baščaršija",

        songs = listOf(
            Music(
                id = "1",
                title = "Coffee Time",
                artist = "Sarajevo Jazz",
                duration = "3:45",
                albumImage = R.drawable.album1
            ),

            Music(
                id = "2",
                title = "Morning Walk",
                artist = "City Lights",
                duration = "4:10",
                albumImage = R.drawable.album1
            )
        ),

        likes = 240
    )


    private val comments = listOf(

        Comment(
            id = "1",
            user = user,
            text = "This outfit fits the vibe perfectly 🔥",
            time = "5 min ago",
            likes = 12
        ),

        Comment(
            id = "2",
            user = user,
            text = "The playlist choice is amazing ☕",
            time = "10 min ago",
            likes = 8
        )

    )


    fun getPosts(): List<FeedPost>{

        return listOf(

            FeedPost(
                id = "1",
                user = user,
                image = R.drawable.outfit1,
                caption = "Perfect weather for coffee downtown ☕",
                weather = weather,
                playlist = playlist,
                time = "12 min ago",
                likes = 128,
                comments = 24
            )

        )

    }



    fun getComments(postId: String): List<Comment>{

        return comments

    }

}