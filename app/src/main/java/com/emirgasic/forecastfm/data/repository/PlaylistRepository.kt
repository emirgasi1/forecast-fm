package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Music
import com.emirgasic.forecastfm.data.model.Playlist

class PlaylistRepository {


    private val morningSongs = listOf(

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

    )


    private val nightSongs = listOf(

        Music(
            id = "3",
            title = "Night Drive",
            artist = "Unknown Artist",
            duration = "3:20",
            albumImage = R.drawable.album2
        ),

        Music(
            id = "4",
            title = "Late Coffee",
            artist = "Sarajevo Vibes",
            duration = "2:55",
            albumImage = R.drawable.album2
        )

    )


    private val hipHopSongs = listOf(

        Music(
            id = "5",
            title = "Street Lights",
            artist = "Lil Nameless",
            duration = "3:30",
            albumImage = R.drawable.album3
        ),

        Music(
            id = "6",
            title = "Night Ride",
            artist = "Lil Nameless",
            duration = "4:05",
            albumImage = R.drawable.album3
        )

    )


    fun getPlaylists(): List<Playlist> {

        return listOf(

            Playlist(
                id = "1",
                title = "Morning Coffee",
                genre = "Jazz",
                mood = "Relax",
                albumImage = R.drawable.album1,
                weather = "Sunny",
                temperature = "22°C",
                location = "Baščaršija",
                songs = morningSongs,
                likes = 240
            ),


            Playlist(
                id = "2",
                title = "GoodNight Lovell",
                genre = "Lo-Fi",
                mood = "Cozy Night",
                albumImage = R.drawable.album2,
                weather = "Rain",
                temperature = "8°C",
                location = "Otoka",
                songs = nightSongs,
                likes = 180
            ),


            Playlist(
                id = "3",
                title = "Lil Nameless 2K16",
                genre = "Hip-Hop",
                mood = "Night Drive",
                albumImage = R.drawable.album3,
                weather = "Cloudy",
                temperature = "15°C",
                location = "Dobrinja",
                songs = hipHopSongs,
                likes = 320
            )

        )
    }
}