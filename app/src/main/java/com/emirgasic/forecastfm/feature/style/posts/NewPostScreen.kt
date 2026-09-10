package com.emirgasic.forecastfm.feature.style.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostScreen(
    navController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: NewPostViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return NewPostViewModel(tokenManager) as T
            }
        }
    )
) {
    val newPost by viewModel.newPost.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val post = newPost ?: return

    val imagePicker = rememberImagePicker { uri ->
        uri?.let {
            viewModel.updateImage(it.toString())
        }
    }
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 60.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Text(
                    text = "New Post",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Spacer(modifier.height(20.dp))
            }

            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            item {
                ImagePickerCard(
                    image = post.image,
                    icon = painterResource(R.drawable.camera),
                    text = if (post.image != null) "Change photo" else "Add a photo",
                    onClick = {
                        imagePicker.pickFromGallery()
                    }
                )
            }

            item {
                Spacer(modifier.height(18.dp))
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
                Spacer(modifier.height(18.dp))
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
                Spacer(modifier.height(18.dp))
            }

            item {
                Text(
                    text = "Location",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Spacer(modifier.height(6.dp))
            }

            item {
                OutlinedTextField(
                    value = post.location,
                    onValueChange = {
                        viewModel.updateLocation(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Baščaršija")
                    },
                    singleLine = true
                )
            }

            item {
                Spacer(modifier.height(18.dp))
            }

            item {
                Text(
                    text = "Playlist",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Spacer(modifier.height(6.dp))
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
                Spacer(modifier.height(18.dp))
            }

            item {
                PostActionButtons(
                    onPostClick = {
                        viewModel.createPost(
                            onSuccess = {
                                navController.popBackStack()
                            },
                            contentResolver = contentResolver
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