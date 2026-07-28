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
import androidx.compose.material3.Icon
@Composable
fun ProfileScreen(mainNavController: NavController, rootNavController: NavController,modifier: Modifier=Modifier){
    val posts = listOf(
        "1st Post",
        "2nd Post",
        "3rd Post",
        "4th Post"
    )
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 20.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    ){
        LazyColumn(modifier=Modifier.fillMaxSize(),verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally){
            item{
            Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Top){
                Text(text="Style",
                    color= MaterialTheme.colorScheme.onBackground,
                    style= MaterialTheme.typography.headlineSmall)
                Spacer(modifier.weight(1f))
                IconButton(
                    onClick = {
                        rootNavController.navigate(Routes.Settings)
                    }
                ) {
                    Image(
                        painter = painterResource(R.drawable.cogwheel),
                        contentDescription = "Settings",
                        modifier = Modifier.size(30.dp)
                    )
                }

            }}
            item{
            Spacer(modifier.height(18.dp))}
            item{
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(R.drawable.profile_picture),
                    contentDescription = "Profile picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }}
            item{
            Spacer(modifier.height(18.dp))}
            item{
            Text(text="Emir",color= MaterialTheme.colorScheme.onPrimary,style= MaterialTheme.typography.headlineMedium)}
            item{
            Spacer(modifier.height(10.dp))}
            item {
                Text(
                    text = "Coffee, music & Sarajevo",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            item {
                Spacer(modifier.height(28.dp))
            }
            item{

            Card(modifier=Modifier.fillMaxWidth(),colors= CardDefaults.cardColors(containerColor= MaterialTheme.colorScheme.surfaceVariant),
                border= BorderStroke(width=1.dp, color= MaterialTheme.colorScheme.outline),
                shape=MaterialTheme.shapes.medium) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly){
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text="24",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        Text(text="Likes",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleLarge)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text="19",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        Text(text="Saved",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleLarge)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text="104",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        Text(text="Posts",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleLarge)
                    }
                }

            }
            }
            item {
                Spacer(modifier.height(28.dp))
            }
            item{
                Text(text="Favorite Playlist",
                    color=MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineSmall)

            }
            item{
                Spacer(modifier.height(18.dp))
            }
            item{
                Card(modifier=Modifier.fillMaxWidth(),colors= CardDefaults.cardColors(containerColor= MaterialTheme.colorScheme.surfaceVariant),
                        border= BorderStroke(width=1.dp, color= MaterialTheme.colorScheme.outline),
                        shape=MaterialTheme.shapes.medium) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(painter=painterResource(R.drawable.music), contentDescription = "Music",modifier=Modifier.size(20.dp))
                        Spacer(modifier.width(28.dp))
                        Text(
                            text = "Chill Pop",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            item{
                Spacer(modifier.height(12.dp))
            }
            item{
                Card(modifier=Modifier.fillMaxWidth(),colors= CardDefaults.cardColors(containerColor= MaterialTheme.colorScheme.surfaceVariant),
                    border= BorderStroke(width=1.dp, color= MaterialTheme.colorScheme.outline),
                    shape=MaterialTheme.shapes.medium) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(painter=painterResource(R.drawable.music), contentDescription = "Music",modifier=Modifier.size(20.dp))
                        Spacer(modifier.width(28.dp))
                        Text(
                            text = "Sarajevo Nights",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            item{Spacer(modifier.height(20.dp))
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                    Text(
                        text = "Posts",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier.width(4.dp))
                    IconButton(
                        onClick = {
                            rootNavController.navigate(Routes.NewPost)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Photo"
                        )
                    }
                }
            }
            item{Spacer(modifier.height(20.dp))
            }
            items(posts.chunked(2)) { rowPosts ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    rowPosts.forEach { post ->

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(180.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .background(
                                            MaterialTheme.colorScheme.surface
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Text(
                                        text = "Post Image"
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Text(
                                    text = post,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.titleMedium
                                )

                            }
                        }
                    }
                }
            }


        }
    }
}
