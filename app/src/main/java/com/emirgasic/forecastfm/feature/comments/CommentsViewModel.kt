package com.emirgasic.forecastfm.feature.comments

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Comment
import com.emirgasic.forecastfm.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class CommentsViewModel : ViewModel() {


    private val _comments = MutableStateFlow(
        listOf(

            Comment(
                id = "1",

                user = User(
                    id = "1",
                    username = "Emir",
                    bio = "Coffee, music & Sarajevo",
                    profileImage = R.drawable.outfit2,
                    favoriteLocation = "Baščaršija",
                    likes = 24,
                    saved = 19,
                    posts = 104
                ),

                text = "Nice outfit!",

                time = "5 min ago",

                likes = 12
            ),


            Comment(
                id = "2",

                user = User(
                    id = "2",
                    username = "Sara",
                    bio = "Music lover",
                    profileImage = R.drawable.outfit1,
                    favoriteLocation = "Ilidža",
                    likes = 30,
                    saved = 15,
                    posts = 80
                ),

                text = "This playlist fits perfectly 🔥",

                time = "10 min ago",

                likes = 8
            )

        )
    )


    val comments: StateFlow<List<Comment>> =
        _comments.asStateFlow()



    fun addComment(text: String) {

        if (text.isBlank()) return


        val newComment = Comment(

            id = System.currentTimeMillis().toString(),

            user = User(
                id = "current_user",
                username = "Emir",
                bio = "Coffee, music & Sarajevo",
                profileImage = R.drawable.profile_picture,
                favoriteLocation = "Baščaršija",
                likes = 0,
                saved = 0,
                posts = 0
            ),

            text = text,

            time = "Just now",

            likes = 0

        )


        _comments.value = _comments.value + newComment

    }

}