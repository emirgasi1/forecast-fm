package com.emirgasic.forecastfm.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.navigation.Routes
import androidx.compose.foundation.clickable
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.profile.FavoritePlaylistCard
import com.emirgasic.forecastfm.core.ui.components.profile.ProfileHeader
import com.emirgasic.forecastfm.core.ui.components.profile.ProfilePostCard
import com.emirgasic.forecastfm.core.ui.components.profile.ProfileStatsCard
import androidx.compose.runtime.getValue

@Composable
fun ProfileScreen(
    rootNavController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 20.dp,
                start = 10.dp,
                bottom = 10.dp,
                end = 10.dp
            )
    ) {

        when (val state = uiState) {

            ProfileUiState.Loading -> {

                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
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
                        text = "Couldn't load profile"
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
                            viewModel.loadProfile()
                        }
                    ) {
                        Text("Retry")
                    }
                }
            }

            is ProfileUiState.Success -> {

                val profileData = state.profile

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {

                            Text(
                                text = "Style",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Spacer(
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {
                                    rootNavController.navigate(
                                        Routes.Settings
                                    )
                                }
                            ) {

                                Image(
                                    painter = painterResource(
                                        R.drawable.cogwheel
                                    ),
                                    contentDescription = "Settings",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )
                    }

                    item {

                        ProfileHeader(
                            image = painterResource(
                                profileData.profileImage
                            ),
                            username = profileData.username,
                            bio = profileData.bio
                        )
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(28.dp)
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
                        Spacer(
                            modifier = Modifier.height(28.dp)
                        )
                    }

                    item {

                        SectionTitle(
                            title = "Favorite Playlist"
                        )
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )
                    }

                    items(profileData.favoritePlaylists) { playlist ->

                        FavoritePlaylistCard(
                            icon = painterResource(
                                R.drawable.music
                            ),
                            playlist = playlist
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )
                    }

                    item {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            SectionTitle(
                                title = "Posts"
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            IconButton(
                                onClick = {
                                    rootNavController.navigate(
                                        Routes.NewPost
                                    )
                                }
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Photo"
                                )
                            }
                        }
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )
                    }

                    items(
                        profileData.profilePosts.chunked(2)
                    ) { rowPosts ->

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            rowPosts.forEach { post ->

                                ProfilePostCard(
                                    modifier = Modifier.weight(1f),
                                    title = post.caption,
                                    imageUrl=post.imageUrl,
                                    onClick = { }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}