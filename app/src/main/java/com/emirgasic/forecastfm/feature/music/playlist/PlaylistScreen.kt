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
                        Spacer(modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically){

                            Image(painter = painterResource(R.drawable.sun),
                                contentDescription = "Sun",
                                modifier.size(20.dp))
                            Spacer(modifier.width(4.dp))
                            Text(text="Sunny",
                                color=MaterialTheme.colorScheme.onBackground,
                                style=MaterialTheme.typography.bodyLarge)
                            Spacer(modifier.width(18.dp))
                            Text(text="22°C",
                                color=MaterialTheme.colorScheme.onBackground,
                                style=MaterialTheme.typography.bodyLarge)
                            Spacer(modifier.width(18.dp))
                            Image(painter = painterResource(R.drawable.mappin),
                                contentDescription = "Location",
                                modifier.size(20.dp))
                            Spacer(modifier.width(4.dp))
                            Text(text="Bascarsija",
                                color=MaterialTheme.colorScheme.onBackground,
                                style=MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
            item{
                Spacer(modifier.height(28.dp))
            }
            item{
                Column(modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top) {
                    Text(text="Best For",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineSmall)
                }
            }
            item{
                Spacer(modifier.height(12.dp))
            }
            item{
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )){
                    Row(modifier=Modifier.fillMaxWidth()
                        .background(color= MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top){
                        Image(painter=painterResource(R.drawable.coffee), contentDescription = "Coffee",
                            Modifier.size(20.dp))
                        Spacer(modifier.width(8.dp))
                        Text(text="Morning Coffee",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium)
                    }

                }
            }
            item{
                Spacer(modifier.height(12.dp))
            }
            item{
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )){
                    Row(modifier=Modifier.fillMaxWidth()
                        .background(color= MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top){
                        Image(painter=painterResource(R.drawable.books), contentDescription = "Coffee",
                            Modifier.size(20.dp))
                        Spacer(modifier.width(8.dp))
                        Text(text="Studying",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium)
                    }

                }
            }
            item{
                Spacer(modifier.height(12.dp))
            }
            item{
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )){
                    Row(modifier=Modifier.fillMaxWidth()
                        .background(color= MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top){
                        Image(painter=painterResource(R.drawable.moon), contentDescription = "Coffee",
                            Modifier.size(20.dp))
                        Spacer(modifier.width(8.dp))
                        Text(text="Late Night Walk",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium)
                    }

                }
            }
            item{
                Spacer(modifier.height(28.dp))
            }

            item{
                Column(modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top) {
                    Text(text="Would You Rather",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineSmall)
                }
            }
            item{
                Spacer(modifier.height(12.dp))
            }
            item{
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )){
                    Row(modifier=Modifier.fillMaxWidth()
                        .background(color= MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top){
                        Image(painter=painterResource(R.drawable.music), contentDescription = "Coffee",
                            Modifier.size(20.dp))
                        Spacer(modifier.width(8.dp))
                        Text(text="Open in Spotify",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium)
                    }

                }
            }
            item{
                Spacer(modifier.height(12.dp))
            }
            item{
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )){
                    Row(modifier=Modifier.fillMaxWidth()
                        .background(color= MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top){
                        Image(painter=painterResource(R.drawable.play), contentDescription = "Coffee",
                            Modifier.size(20.dp))
                        Spacer(modifier.width(8.dp))
                        Text(text="Open in Youtube",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium)
                    }

                }
            }
            item{
                Spacer(modifier.height(28.dp))
            }

            item{
                Column(modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top) {
                    Text(text="Similar Playlists",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineSmall)
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
                        Image(painter=painterResource(R.drawable.album2),
                            contentDescription = "album_cover",
                            modifier=Modifier.size(300.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .border(width=1.dp,color= MaterialTheme.colorScheme.outline.copy(alpha=0.3f)
                                    ,shape= MaterialTheme.shapes.medium))
                        Text(text="GoodNight Lovell",
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
                        Spacer(modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically){

                            Image(painter = painterResource(R.drawable.heavy_rain),
                                contentDescription = "Rain",
                                modifier.size(20.dp))
                            Spacer(modifier.width(4.dp))
                            Text(text="Sunny",
                                color=MaterialTheme.colorScheme.onBackground,
                                style=MaterialTheme.typography.bodyLarge)
                            Spacer(modifier.width(18.dp))
                            Text(text="-4°C",
                                color=MaterialTheme.colorScheme.onBackground,
                                style=MaterialTheme.typography.bodyLarge)
                            Spacer(modifier.width(18.dp))
                            Image(painter = painterResource(R.drawable.mappin),
                                contentDescription = "Location",
                                modifier.size(20.dp))
                            Spacer(modifier.width(4.dp))
                            Text(text="Otoka",
                                color=MaterialTheme.colorScheme.onBackground,
                                style=MaterialTheme.typography.bodyLarge)
                        }
                    }
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
                        Image(painter=painterResource(R.drawable.album3),
                            contentDescription = "album_cover",
                            modifier=Modifier.size(300.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .border(width=1.dp,color= MaterialTheme.colorScheme.outline.copy(alpha=0.3f)
                                    ,shape= MaterialTheme.shapes.medium))
                        Text(text="Lil Nameless 2k16",
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
                        Spacer(modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically){

                            Image(painter = painterResource(R.drawable.sunny_cloudy),
                                contentDescription = "Sun_Cloudy",
                                modifier.size(20.dp))
                            Spacer(modifier.width(4.dp))
                            Text(text="Sunny",
                                color=MaterialTheme.colorScheme.onBackground,
                                style=MaterialTheme.typography.bodyLarge)
                            Spacer(modifier.width(18.dp))
                            Text(text="22°C",
                                color=MaterialTheme.colorScheme.onBackground,
                                style=MaterialTheme.typography.bodyLarge)
                            Spacer(modifier.width(18.dp))
                            Image(painter = painterResource(R.drawable.mappin),
                                contentDescription = "Location",
                                modifier.size(20.dp))
                            Spacer(modifier.width(4.dp))
                            Text(text="Dobrinja",
                                color=MaterialTheme.colorScheme.onBackground,
                                style=MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }

    }



    }

