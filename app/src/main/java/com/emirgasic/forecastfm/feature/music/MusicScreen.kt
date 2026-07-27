package com.emirgasic.forecastfm.feature.music

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.emirgasic.forecastfm.core.navigation.Routes

@Composable
fun MusicScreen(mainNavController: NavController,
                rootNavController: NavController,
                modifier: Modifier =Modifier) {
    var search by remember {
        mutableStateOf("")
    }
    var selectedGenre by remember{
        mutableStateOf("")
    }
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 20.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            item{Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(R.drawable.music),
                    contentDescription = "Music",
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "Music",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall
                )
            }}
            item{
            Spacer(modifier = Modifier.height(20.dp))
            }
            item{
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("search playlists...") },
                    leadingIcon = {
                        Image(
                            painter = painterResource(R.drawable.magnify),
                            contentDescription = "Search",
                            modifier = Modifier.size(20.dp)
                        )
                    }, singleLine = true
                )
            }
            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
            item{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(R.drawable.sun),
                        contentDescription = "Sun",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "For Today's Weather",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
            item{
                Text(
                    text = "Because it's Sunny and 24°C",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            item{
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                ) {
                    Column(
                        modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Summer Walks",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Chill Pop",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Text(
                                text = "18 songs",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "1h 12min",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )

                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.heart),
                                contentDescription = "Heart",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier.width(6.dp))
                            Text(
                                "124",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier.weight(1f))
                            Image(
                                painter = painterResource(R.drawable.play),
                                contentDescription = "PlayButton",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier.width(6.dp))
                            Text(
                                "Play",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier.height(16.dp))
            }
            item{
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                ) {
                    Column(
                        modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Golden Hour",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Indie",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Text(
                                text = "18 songs",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "1h 12min",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )

                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.heart),
                                contentDescription = "Heart",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier.width(6.dp))
                            Text(
                                "87",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier.weight(1f))
                            Image(
                                painter = painterResource(R.drawable.play),
                                contentDescription = "PlayButton",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier.width(6.dp))
                            Text(
                                "Play",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }


            item {
                Spacer(modifier.height(36.dp))
            }

            item{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(R.drawable.fire),
                        contentDescription = "Trend",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Trending",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
           item{
               Card(
                   modifier = Modifier.fillMaxWidth(),
                   shape = MaterialTheme.shapes.medium,
                   border = BorderStroke(
                       width = 1.dp,
                       color = MaterialTheme.colorScheme.outline
                   ),
               ) {
                   Column(
                       modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant)
                           .padding(16.dp),
                       verticalArrangement = Arrangement.spacedBy(12.dp)
                   ) {
                       Row(
                           modifier = Modifier.fillMaxWidth(),
                           horizontalArrangement = Arrangement.SpaceBetween,
                           verticalAlignment = Alignment.CenterVertically
                       ) {
                           Text(
                               text = "Midnight Coffee",
                               color = MaterialTheme.colorScheme.onPrimary,
                               style = MaterialTheme.typography.titleMedium
                           )

                           Text(
                               text = "Lo-fi",
                               color = MaterialTheme.colorScheme.onPrimary,
                               style = MaterialTheme.typography.titleMedium
                           )
                       }
                       Row(verticalAlignment = Alignment.CenterVertically){
                           Text(
                               text = "18 songs",
                               color = MaterialTheme.colorScheme.onPrimary,
                               style = MaterialTheme.typography.titleMedium
                           )
                           Text(
                               text = "•",
                               style = MaterialTheme.typography.bodyLarge,
                               color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                           )
                           Text(
                               text = "1h 12min",
                               color = MaterialTheme.colorScheme.onPrimary,
                               style = MaterialTheme.typography.titleMedium
                           )

                       }
                       Row(verticalAlignment = Alignment.CenterVertically) {
                           Image(
                               painter = painterResource(R.drawable.heart),
                               contentDescription = "Heart",
                               modifier = Modifier.size(20.dp)
                           )
                           Spacer(modifier.width(6.dp))
                           Text(
                               "452",
                               color = MaterialTheme.colorScheme.onPrimary,
                               style = MaterialTheme.typography.bodyLarge
                           )
                           Spacer(modifier.weight(1f))
                           Image(
                               painter = painterResource(R.drawable.play),
                               contentDescription = "PlayButton",
                               modifier = Modifier.size(20.dp)
                           )
                           Spacer(modifier.width(6.dp))
                           Text(
                               "Play",
                               color = MaterialTheme.colorScheme.onPrimary,
                               style = MaterialTheme.typography.bodyLarge
                           )
                       }
                   }
               }
           }
            item {
                Spacer(modifier.height(16.dp))
            }
            item{
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                ) {
                    Column(
                        modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Downtown Jazz",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Jazz",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Text(
                                text = "18 songs",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "1h 12min",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )

                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.heart),
                                contentDescription = "Heart",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier.width(6.dp))
                            Text(
                                "318",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier.weight(1f))
                            Image(
                                painter = painterResource(R.drawable.play),
                                contentDescription = "PlayButton",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier.width(6.dp))
                            Text(
                                "Play",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

            }
            item {
                Spacer(modifier.height(16.dp))
            }
            item{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(R.drawable.headphones),
                        contentDescription = "Headphones",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Genres",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            item{
                LazyRow(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top) {
                    item{
                        FilterChip(
                            selected=selectedGenre=="Lo-fi",
                            onClick={
                                selectedGenre="Lo-fi"
                            },
                            label={
                                Text(text="Lo-fi")
                            }
                        )
                    }
                    item{
                        FilterChip(
                            selected=selectedGenre=="Jazz",
                            onClick={
                                selectedGenre="Jazz"
                            },
                            label={
                                Text(text="Jazz")
                            }
                        )
                    }
                    item{
                        FilterChip(
                            selected=selectedGenre=="Rock",
                            onClick={
                                selectedGenre="Rock"
                            },
                            label={
                                Text(text="Rock")
                            }
                        )
                    }
                    item{
                        FilterChip(
                            selected=selectedGenre=="Classical",
                            onClick={
                                selectedGenre="Classical"
                            },
                            label={
                                Text(text="Classical")
                            }
                        )
                    }
                    item{
                        FilterChip(
                            selected=selectedGenre=="Pop",
                            onClick={
                                selectedGenre="Pop"
                            },
                            label={
                                Text(text="Pop")
                            }
                        )
                    }
                    item{
                        FilterChip(
                            selected=selectedGenre=="Hip-Pop",
                            onClick={
                                selectedGenre="Hip-Pop"
                            },
                            label={
                                Text(text="Hip-Pop")
                            }
                        )
                    }
                }
            }
            item{
                Spacer(modifier.height(24.dp))
            }
            item{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(R.drawable.stars),
                        contentDescription = "Recommended",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Recommended For You",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            item{
                Spacer(modifier.height(16.dp))
            }
            item{
               Card(modifier = Modifier.fillMaxWidth(),
                   shape = MaterialTheme.shapes.medium,
                   border = BorderStroke(
                       width = 1.dp,
                       color = MaterialTheme.colorScheme.outline
                   )){
                   Column(modifier=Modifier.fillMaxWidth()
                       .background(color= MaterialTheme.colorScheme.surfaceVariant)
                       .padding(16.dp),
                       horizontalAlignment = Alignment.CenterHorizontally,
                       verticalArrangement = Arrangement.spacedBy(16.dp)) {
                       Image(painter=painterResource(R.drawable.album1),
                           contentDescription = "album_cover",
                           modifier=Modifier.size(300.dp)
                               .clip(MaterialTheme.shapes.medium)
                               .border(width=1.dp,color= MaterialTheme.colorScheme.outline.copy(alpha=0.3f)
                               ,shape= MaterialTheme.shapes.medium))
                       Text(text="I No Longer Fear the Razor Guarding My Heel",
                           maxLines = 2,
                           overflow= TextOverflow.Ellipsis,
                           color= MaterialTheme.colorScheme.onPrimary,
                           style=MaterialTheme.typography.titleLarge,
                           textAlign = TextAlign.Center)
                       Row(modifier = Modifier.fillMaxWidth(),
                           horizontalArrangement = Arrangement.Center,
                           verticalAlignment = Alignment.CenterVertically){
                           Text(text="Lo-fi",
                               color=MaterialTheme.colorScheme.onBackground,
                               style=MaterialTheme.typography.bodyLarge)
                           Spacer(modifier.width(8.dp))
                           Text(
                               text = "•",
                               style = MaterialTheme.typography.bodyLarge,
                               color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                           )
                           Spacer(modifier.width(8.dp))
                           Text(text="Cozy Night",
                               color=MaterialTheme.colorScheme.onBackground,
                               style=MaterialTheme.typography.bodyLarge)
                       }
                       Row(modifier=Modifier.fillMaxWidth(),
                           horizontalArrangement = Arrangement.Center,
                           verticalAlignment = Alignment.CenterVertically){
                           Image(
                               painter = painterResource(R.drawable.play),
                               contentDescription = "PlayButton",
                               modifier = Modifier.size(20.dp)
                           )
                           Spacer(modifier.width(6.dp))
                           Text(
                               "Play",
                               color = MaterialTheme.colorScheme.onPrimary,
                               style = MaterialTheme.typography.bodyLarge
                           )
                           Spacer(modifier.width(56.dp))
                           Image(
                               painter = painterResource(R.drawable.heart),
                               contentDescription = "Heart",
                               modifier = Modifier.size(20.dp)
                           )
                           Spacer(modifier.width(6.dp))
                           Text(
                               "124",
                               color = MaterialTheme.colorScheme.onPrimary,
                               style = MaterialTheme.typography.bodyLarge
                           )

                       }

                       Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                           TextButton(
                               onClick = {
                                   rootNavController.navigate(Routes.Playlist)
                               }
                           ) {
                               Text("View Playlist", color = MaterialTheme.colorScheme.primary,
                                   style= MaterialTheme.typography.bodyLarge)
                           }
                       }
                   }
               }
            }


        }
    }
}