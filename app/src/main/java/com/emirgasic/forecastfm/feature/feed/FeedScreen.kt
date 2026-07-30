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


@Composable
fun FeedScreen(
    mainNavController: NavController,
    rootNavController: NavController,
    modifier: Modifier = Modifier
) {

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

            item {
                FeedPostCard(
                    profileImage = painterResource(R.drawable.profile_picture),
                    username = "Emir",
                    time = "12 min ago",
                    weatherIcon = painterResource(R.drawable.sun),
                    weather = "Sunny",
                    temperature = "22°C",
                    location = "Baščaršija",
                    postImage = painterResource(R.drawable.outfit1),
                    playlist = "Morning Coffee Jazz",
                    caption = "Perfect weather for coffee and a walk through Sarajevo ☕",
                    likes = "128",
                    comments = "24",
                    onCommentClick = {
                        rootNavController.navigate(Routes.Comments)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                FeedPostCard(
                    profileImage = painterResource(R.drawable.profile_picture),
                    username = "Sarah",
                    time = "45 min ago",
                    weatherIcon = painterResource(R.drawable.heavy_rain),
                    weather = "Rainy",
                    temperature = "9°C",
                    location = "Ilidža",
                    postImage = painterResource(R.drawable.outfit2),
                    playlist = "GoodNight Lovell",
                    caption = "Rainy day outfit 🌧️",
                    likes = "94",
                    comments = "17",
                    onCommentClick = {
                        rootNavController.navigate(Routes.Comments)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                FeedPostCard(
                    profileImage = painterResource(R.drawable.profile_picture),
                    username = "Alex",
                    time = "2 hrs ago",
                    weatherIcon = painterResource(R.drawable.sunny_cloudy),
                    weather = "Cloudy",
                    temperature = "18°C",
                    location = "Dobrinja",
                    postImage = painterResource(R.drawable.outfit3),
                    playlist = "Lil Nameless 2k16",
                    caption = "Comfortable fit for today's weather.",
                    likes = "201",
                    comments = "41",
                    onCommentClick = {
                        rootNavController.navigate(Routes.Comments)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}