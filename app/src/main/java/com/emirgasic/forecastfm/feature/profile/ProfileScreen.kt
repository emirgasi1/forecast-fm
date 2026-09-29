package com.emirgasic.forecastfm.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.emirgasic.forecastfm.core.ui.components.profile.FavoritePlaylistCard
import com.emirgasic.forecastfm.core.ui.components.profile.ProfileHeader
import com.emirgasic.forecastfm.core.ui.components.profile.ProfilePostCard
import com.emirgasic.forecastfm.core.ui.components.profile.ProfileStatsCard
import com.emirgasic.forecastfm.data.repository.ProfileRepository
import com.emirgasic.forecastfm.network.profile.ProfileApi
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun ProfileScreen(
    rootNavController: NavController,
    modifier: Modifier = Modifier,
    tokenManager: TokenManager,
    viewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(
                    tokenManager = tokenManager,
                    profileRepository = ProfileRepository(ProfileApi())
                ) as T
            }
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val forecastColors = LocalForecastColors.current

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 20.dp, start = 10.dp, end = 10.dp)
    ) {

        when (val state = uiState) {

            ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingScreen()
                }
            }

            is ProfileUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Couldn't load profile",
                        color = forecastColors.title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = state.message,
                        color = forecastColors.muted,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.loadProfile() }
                    ) {
                        Text("Retry")
                    }
                }
            }

            is ProfileUiState.Success -> {

                val profileData = state.profile

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {

                            Text(
                                text = "Profile",
                                color = forecastColors.title,
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(
                                onClick = {
                                    rootNavController.navigate(Routes.Settings)
                                }
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.cogwheel),
                                    contentDescription = "Settings",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    item {
                        ProfileHeader(
                            image = profileData.profileImage,
                            username = profileData.username,
                            bio = profileData.bio
                        )
                    }

                    item {
                        ProfileStatsCard(
                            likes = profileData.likes.toString(),
                            saved = profileData.saved.toString(),
                            posts = profileData.posts.toString()
                        )
                    }

                    item {
                        SectionTitle(title = "Favorite Playlist")
                    }

                    items(profileData.favoritePlaylists) { playlist ->
                        FavoritePlaylistCard(
                            icon = painterResource(R.drawable.music),
                            playlist = playlist
                        )
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            SectionTitle(title = "Posts")

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = {
                                    rootNavController.navigate(Routes.NewPost)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Photo",
                                    tint = forecastColors.primary
                                )
                            }
                        }
                    }

                    items(profileData.profilePosts.chunked(2)) { rowPosts ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            rowPosts.forEach { post ->
                                ProfilePostCard(
                                    modifier = Modifier.weight(1f),
                                    title = post.caption,
                                    imageUrl = post.imageUrl,
                                    onClick = { }
                                )
                            }

                            if (rowPosts.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}