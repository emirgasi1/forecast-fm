package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.model.NewPost
class NewPostRepository {

    fun getNewPostData(): NewPost {

        return NewPost(

            image = null,

            caption = "",

            weather = "",

            location = "",

            selectedPlaylist = "Jazz",

            playlists = listOf(
                "Jazz",
                "Rock",
                "Metal",
                "Balkan",
                "Hip-Hop"
            )

        )

    }

}