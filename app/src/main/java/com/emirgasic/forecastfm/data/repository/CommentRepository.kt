package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Comment
import com.emirgasic.forecastfm.data.model.User

class CommentRepository {

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

    fun getComments(): List<Comment> {
        return listOf(
            Comment(
                id = "1",
                user = user,
                text = "Love this outfit!",
                time = "2 min ago",
                likes = 12
            ),
            Comment(
                id = "2",
                user = user,
                text = "Perfect playlist for today.",
                time = "10 min ago",
                likes = 5
            )
        )
    }
}