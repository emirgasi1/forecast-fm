package com.emirgasic.forecastfm.feature.feed

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.data.model.Comment
import com.emirgasic.forecastfm.data.model.FeedPost
import com.emirgasic.forecastfm.data.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class FeedViewModel : ViewModel() {


    private val repository = FeedRepository()



    private val _posts = MutableStateFlow<List<FeedPost>>(emptyList())
    val posts: StateFlow<List<FeedPost>> = _posts.asStateFlow()



    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()



    init {
        loadFeed()
    }



    private fun loadFeed(){

        _posts.value = repository.getPosts()

    }



    fun loadComments(postId: String){

        _comments.value = repository.getComments(postId)

    }



    fun addComment(comment: Comment){

        val currentComments = _comments.value.toMutableList()

        currentComments.add(comment)

        _comments.value = currentComments

    }

}