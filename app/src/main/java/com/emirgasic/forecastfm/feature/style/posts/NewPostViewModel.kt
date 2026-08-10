package com.emirgasic.forecastfm.feature.style.posts

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.data.model.NewPost
import com.emirgasic.forecastfm.data.repository.NewPostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NewPostViewModel : ViewModel() {

    private val repository = NewPostRepository()

    private val _newPost = MutableStateFlow<NewPost?>(null)
    val newPost: StateFlow<NewPost?> = _newPost.asStateFlow()


    init {
        loadNewPost()
    }


    private fun loadNewPost() {

        _newPost.value = repository.getNewPostData()

    }

    fun updateImage(image: String?) {

        _newPost.value = _newPost.value?.copy(
            image = image
        )

    }

    fun updateCaption(value: String) {

        _newPost.value = _newPost.value?.copy(
            caption = value
        )

    }


    fun updateWeather(value: String) {

        _newPost.value = _newPost.value?.copy(
            weather = value
        )

    }


    fun updateLocation(value: String) {

        _newPost.value = _newPost.value?.copy(
            location = value
        )

    }


    fun selectPlaylist(value: String) {

        _newPost.value = _newPost.value?.copy(
            selectedPlaylist = value
        )

    }

}