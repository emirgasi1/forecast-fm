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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.common.LoadingScreen
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.feed.FeedPostCard
import com.emirgasic.forecastfm.core.ui.components.feed.SaveOptionsBottomSheet
import com.emirgasic.forecastfm.data.repository.CommentRepository
import com.emirgasic.forecastfm.data.repository.FeedRepository
import com.emirgasic.forecastfm.data.repository.LikeRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.PostRepository
import com.emirgasic.forecastfm.data.repository.SavedOutfitRepository
import com.emirgasic.forecastfm.data.repository.SavedPostRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.network.comment.CommentApi
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import com.emirgasic.forecastfm.network.post.PostApi
import com.emirgasic.forecastfm.network.user.UserApi

@Composable
fun FeedScreen(
    mainNavController: NavController,
    rootNavController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                val userApi = UserApi()
                val userRepository = UserRepository(userApi)
                val postApi = PostApi()
                val postRepository = PostRepository(postApi)
                val playlistApi = PlaylistApi()
                val playlistRepository = PlaylistRepository(playlistApi)
                val commentApi = CommentApi()
                val commentRepository = CommentRepository(commentApi, userRepository)

                return FeedViewModel(
                    tokenManager = tokenManager,
                    feedRepository = FeedRepository(
                        userRepository = userRepository,
                        postRepository = postRepository,
                        playlistRepository = playlistRepository,
                        commentRepository = commentRepository
                    ),
                    likeRepository = LikeRepository(),
                    savedPostRepository = SavedPostRepository(),
                    savedOutfitRepository = SavedOutfitRepository(),
                    outfitRepository = OutfitRepository(),
                    playlistRepository = playlistRepository
                ) as T
            }
        }
    )
) {

    val uiState by viewModel.uiState.collectAsState()
    val likedPosts by viewModel.likedPosts.collectAsState()
    val savedPosts by viewModel.savedPosts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }

    var showSaveSheet by remember { mutableStateOf(false) }
    var selectedPostId by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 20.dp,
                start = 8.dp,
                end = 8.dp,
                bottom = 10.dp
            )
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(28.dp),
            horizontalAlignment = Alignment.Start
        ) {

            item {
                SectionTitle(
                    title = "Feed"
                )
            }

            when (val state = uiState) {

                FeedUiState.Loading -> {
                    item {
                        LoadingScreen()
                    }
                }

                is FeedUiState.Success -> {
                    items(state.posts) { post ->
                        val isLiked = post.id in likedPosts
                        val isSaved = post.id in savedPosts

                        FeedPostCard(
                            profileImage = post.user.profileImage,
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
                            Text(text = "Couldn't load feed")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = state.message)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadFeed() }
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