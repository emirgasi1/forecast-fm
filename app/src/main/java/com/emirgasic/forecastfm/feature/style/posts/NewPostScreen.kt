package com.emirgasic.forecastfm.feature.style.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.ui.components.style.posts.DropdownSelector
import com.emirgasic.forecastfm.core.ui.components.style.posts.ImagePickerCard
import com.emirgasic.forecastfm.core.ui.components.style.posts.PostActionButtons
import com.emirgasic.forecastfm.core.ui.components.style.posts.ProfileInputField
import com.emirgasic.forecastfm.core.utils.rememberImagePicker
import com.emirgasic.forecastfm.data.repository.NewPostRepository
import com.emirgasic.forecastfm.network.image.ImageUploadApi
import com.emirgasic.forecastfm.network.post.PostApi
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun NewPostScreen(
    navController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: NewPostViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return NewPostViewModel(
                    tokenManager = tokenManager,
                    newPostRepository = NewPostRepository(),
                    postApi = PostApi(),
                    imageUploadApi = ImageUploadApi()
                ) as T
            }
        }
    )
) {
    val newPost by viewModel.newPost.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNewPost()
    }

    val post = newPost ?: return

    val imagePicker = rememberImagePicker { uri ->
        uri?.let {
            viewModel.updateImage(it.toString())
        }
    }
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val imageUploadApi = remember { ImageUploadApi() }
    val forecastColors = LocalForecastColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 60.dp, start = 10.dp, end = 10.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Text(
                    text = "New Post",
                    color = forecastColors.title,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage ?: "",
                        color = forecastColors.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                ImagePickerCard(
                    image = post.image,
                    icon = painterResource(R.drawable.clothes),
                    text = if (post.image != null) "Change photo" else "Add a photo",
                    onClick = {
                        imagePicker.pickFromGallery()
                    }
                )
            }

            item {
                ProfileInputField(
                    title = "Caption",
                    value = post.caption,
                    placeholder = "What's today's vibe?",
                    onValueChange = {
                        viewModel.updateCaption(it)
                    }
                )
            }

            item {
                ProfileInputField(
                    title = "Weather",
                    value = post.weather,
                    placeholder = "Sunny",
                    onValueChange = {
                        viewModel.updateWeather(it)
                    }
                )
            }

            item {
                ProfileInputField(
                    title = "Location",
                    value = post.location,
                    placeholder = "Baščaršija",
                    onValueChange = {
                        viewModel.updateLocation(it)
                    }
                )
            }

            item {
                DropdownSelector(
                    title = "Playlist",
                    selected = post.selectedPlaylist,
                    options = post.playlists,
                    onSelected = {
                        viewModel.selectPlaylist(it)
                    }
                )
            }

            item {
                PostActionButtons(
                    onPostClick = {
                        viewModel.createPost(
                            uploadImage = { postId, imageUri ->
                                imageUploadApi.uploadImage(postId, contentResolver, imageUri.toString())
                            },
                            onSuccess = {
                                navController.popBackStack()
                            }
                        )
                    },
                    onDeleteClick = {
                        navController.popBackStack()
                    },
                    isLoading = isLoading
                )
            }
        }
    }
}