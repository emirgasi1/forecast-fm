package com.emirgasic.forecastfm.feature.music.playlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.music.playlist.ExternalMusicLinkCard
import com.emirgasic.forecastfm.core.ui.components.music.playlist.PlaylistHeaderCard
import com.emirgasic.forecastfm.core.ui.components.music.playlist.PlaylistTagCard
import com.emirgasic.forecastfm.core.ui.components.music.playlist.SimilarPlaylistCard

@Composable
fun PlaylistScreen(navController: NavController,modifier: Modifier =Modifier){
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 60.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {

                PlaylistHeaderCard(
                    album = painterResource(R.drawable.album1),
                    title = "I No Longer Fear the Razor Guarding My Heel",
                    genre = "Lo-fi",
                    mood = "Cozy Night",
                    weatherIcon = painterResource(R.drawable.sun),
                    weather = "Sunny",
                    temperature = "22°C",
                    locationIcon = painterResource(R.drawable.mappin),
                    location = "Baščaršija"
                )

            }
            item{
                Spacer(modifier.height(28.dp))
            }
            item {

                SectionTitle(
                    title = "Best For"
                )

            }
            item{
                Spacer(modifier.height(12.dp))
            }
            item {

                PlaylistTagCard(
                    icon = painterResource(R.drawable.coffee),
                    title = "Morning Coffee"
                )

            }
            item{
                Spacer(modifier.height(12.dp))
            }
            item {

                PlaylistTagCard(
                    icon = painterResource(R.drawable.books),
                    title = "Studying"
                )

            }
            item{
                Spacer(modifier.height(12.dp))
            }
            item {

                PlaylistTagCard(
                    icon = painterResource(R.drawable.moon),
                    title = "Late Night Walk"
                )

            }
            item{
                Spacer(modifier.height(28.dp))
            }

            item{
                SectionTitle(
                    title = "Would You Rather"
                )
            }
            item{
                Spacer(modifier.height(12.dp))
            }
            item {

                ExternalMusicLinkCard(
                    icon = painterResource(R.drawable.music),
                    title = "Open in Spotify"
                )

            }
            item{
                Spacer(modifier.height(12.dp))
            }
            item {

                ExternalMusicLinkCard(
                    icon = painterResource(R.drawable.play),
                    title = "Open in Youtube"
                )

            }
            item{
                Spacer(modifier.height(28.dp))
            }

            item{
                SectionTitle(
                    title = "Similar Playlists"
                )
            }
            item{
                Spacer(modifier.height(16.dp))
            }
            item {

                SimilarPlaylistCard(
                    album = painterResource(R.drawable.album2),
                    title = "GoodNight Lovell",
                    genre = "Lo-fi",
                    mood = "Cozy Night",
                    weatherIcon = painterResource(R.drawable.heavy_rain),
                    weather = "Rainy",
                    temperature = "-4°C",
                    locationIcon = painterResource(R.drawable.mappin),
                    location = "Otoka"
                )

            }
            item{
                Spacer(modifier.height(16.dp))
            }
            item {

                SimilarPlaylistCard(
                    album = painterResource(R.drawable.album3),
                    title = "Lil Nameless 2k16",
                    genre = "Lo-fi",
                    mood = "Cozy Night",
                    weatherIcon = painterResource(R.drawable.sunny_cloudy),
                    weather = "Sunny",
                    temperature = "22°C",
                    locationIcon = painterResource(R.drawable.mappin),
                    location = "Dobrinja"
                )

            }
            }
        }

    }





