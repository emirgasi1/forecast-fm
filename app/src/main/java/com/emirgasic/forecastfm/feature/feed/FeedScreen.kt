package com.emirgasic.forecastfm.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun FeedScreen(
    mainNavController: NavController,
    rootNavController: NavController,
    modifier: Modifier = Modifier,
    viewModel:FeedViewModel=viewModel()
) {

    val posts by viewModel.posts.collectAsState()

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


                items(posts){ post ->

                    FeedPostCard(

                        profileImage = painterResource(post.user.profileImage),

                        username = post.user.username,

                        time = "12 min ago",

                        weatherIcon = painterResource(R.drawable.sun),

                        weather = post.weather.condition,

                        temperature = post.weather.temperature,

                        location = post.weather.location,

                        postImage = painterResource(post.image),

                        playlist = post.playlist.title,

                        caption = post.caption,

                        likes = "128",

                        comments = "24",

                        onCommentClick = {

                            rootNavController.navigate(Routes.Comments)

                        }

                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                }



        }
    }
}