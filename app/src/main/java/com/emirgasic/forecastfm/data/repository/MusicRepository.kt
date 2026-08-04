package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Music
import com.emirgasic.forecastfm.data.model.Playlist


class MusicRepository {


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


    fun getPlaylists(): List<Playlist>{

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
                title = "Night Sarajevo",
                genre = "Lo-fi",
                mood = "Chill",
                albumImage = R.drawable.album2,
                weather = "Clear",
                temperature = "15°C",
                location = "Trebević",
                songs = nightSongs,
                likes = 180
            )

        )

    }


}