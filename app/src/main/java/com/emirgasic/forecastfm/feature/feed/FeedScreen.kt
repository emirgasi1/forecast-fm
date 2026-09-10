package com.emirgasic.forecastfm.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.feed.FeedPostCard
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.emirgasic.forecastfm.feature.feed.FeedViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.ui.components.feed.SaveOptionsBottomSheet
import androidx.compose.runtime.setValue

@Composable
fun FeedScreen(
    mainNavController: NavController,
    rootNavController: NavController,
    tokenManager: TokenManager,  // ← Add this
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return FeedViewModel(tokenManager) as T
            }
        }
    )
){

    val uiState by viewModel.uiState.collectAsState()
    val likedPosts by viewModel.likedPosts.collectAsState()

    val savedPosts by viewModel.savedPosts.collectAsState()

    var showSaveSheet by remember { mutableStateOf(false) }
    var selectedPostId by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 60.dp,
                start = 10.dp,
                end = 10.dp,
                bottom = 10.dp
            )
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            item {
                SectionTitle(
                    title = "Feed"
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            when (val state = uiState) {

                FeedUiState.Loading -> {

                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth()
                        )
                    }
                }

                is FeedUiState.Success -> {

                    items(state.posts) { post ->
                        val isLiked = post.id in likedPosts
                        val isSaved = post.id in savedPosts

                        FeedPostCard(
                            profileImage = painterResource(post.user.profileImage),
                            username = post.user.username,
                            time = post.time,
                            weatherIcon = painterResource(R.drawable.sun),
                            weather = post.weather.condition,
                            temperature = post.weather.temperature,
                            location = post.weather.location,
                            postImage = post.image,
                            playlist = post.playlist?.title ?: "No playlist",
                            caption = post.caption,
                            likes = post.likes.toString(),
                            comments = post.comments.toString(),
                            isLiked = isLiked,
                            isSaved = isSaved,
                            onLikeClick = {
                                viewModel.toggleLike(post.id)
                            },
                            onCommentClick = {
                                rootNavController.navigate(Routes.commentsRoute(post.id))
                            },
                            onSaveClick = {
                                selectedPostId = post.id
                                showSaveSheet = true
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                is FeedUiState.Error -> {

                    item {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "Couldn't load feed"
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = state.message
                            )

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            Button(
                                onClick = {
                                    viewModel.loadFeed()
                                }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
        if (showSaveSheet && selectedPostId != null) {
            SaveOptionsBottomSheet(
                onDismiss = {
                    showSaveSheet = false
                    selectedPostId = null
                },
                onSavePost = {
                    selectedPostId?.let { postId ->
                        viewModel.savePost(postId)
                    }
                },
                onSavePlaylist = {
                    selectedPostId?.let { postId ->
                        viewModel.savePlaylistFromPost(postId)
                    }
                },
                onSaveStyle = {
                    selectedPostId?.let { postId ->
                        viewModel.saveStyleFromPost(postId)
                    }
                }
            )
        }
    }
}