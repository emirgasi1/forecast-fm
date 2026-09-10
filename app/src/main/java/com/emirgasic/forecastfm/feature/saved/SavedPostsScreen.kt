package com.emirgasic.forecastfm.feature.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.feed.FeedPostCard

@Composable
fun SavedPostsScreen(
    navController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: SavedPostsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SavedPostsViewModel(tokenManager) as T
            }
        }
    )
) {
    val savedPosts by viewModel.savedPosts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 60.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "← Back",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                    )
                }

                item {
                    Text(
                        text = "Saved Posts",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                if (savedPosts.isEmpty()) {
                    item {
                        Text(
                            text = "No saved posts yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                } else {
                    items(savedPosts) { post ->
                        FeedPostCard(
                            profileImage = painterResource(R.drawable.profile_picture),
                            username = "User",
                            time = post.createdAt,
                            weatherIcon = painterResource(R.drawable.sun),
                            weather = "Sunny",
                            temperature = "22°C",
                            location = "Sarajevo",
                            postImage = post.imageUrl ?: "",
                            playlist = "No playlist",
                            caption = post.caption ?: "",
                            likes = "0",
                            comments = "0",
                            isLiked = false,
                            isSaved = true,
                            onLikeClick = {},
                            onCommentClick = {
                                navController.navigate(Routes.commentsRoute(post.id))
                            },
                            onSaveClick = {
                                viewModel.unsavePost(post.id)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}