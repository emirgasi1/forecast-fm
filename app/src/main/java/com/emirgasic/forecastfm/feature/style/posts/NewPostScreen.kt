package com.emirgasic.forecastfm.feature.style.posts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostScreen(navController: NavController,modifier: Modifier =Modifier) {
    var caption by remember(){
        mutableStateOf("")
    }
    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedPlaylist by remember {
        mutableStateOf("Jazz")
    }
    val playlists = listOf(
        "Jazz",
        "Rock",
        "Metal",
        "Balkan",
        "Hip-Pop"
    )
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 60.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color= MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ){
            item {
                Text(
                    text = "New Post",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            item {
                Spacer(modifier.height(20.dp))
            }
            item{Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text="Add a photo",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Image(painter=painterResource(R.drawable.camera), contentDescription = "Camera",modifier.size(20.dp))
                }
            }
            }
            item {
                Spacer(modifier.height(18.dp))
            }
            item {
                Text(
                    text = "Caption",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            item {
                Spacer(modifier.height(6.dp))
            }
            item{
            OutlinedTextField(value=caption,
                onValueChange = {caption=it},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("What's today's vibe?") },
                singleLine = true

            )}
            item{
            Spacer(modifier.height(18.dp))}
            item{
            Text(text="Weather",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge)}
            item{
            Spacer(modifier.height(6.dp))}
            item{
            OutlinedTextField(value=caption,
                onValueChange = {caption=it},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Sunny") },
                singleLine = true

            )}
            item {
                Spacer(modifier.height(18.dp))
            }
            item {
                Text(
                    text = "Location",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            item {
                Spacer(modifier.height(6.dp))
            }
            item{OutlinedTextField(value=caption,
                onValueChange = {caption=it},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Bascarsija") },
                singleLine = true

            )}
            item{Spacer(modifier.height(18.dp))}

            item{
            Text(text="Playlist",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge)}
            item{
                Spacer(modifier.height(6.dp))}
            item{
            ExposedDropdownMenuBox(

                expanded = expanded,

                onExpandedChange = {
                    expanded = !expanded
                } ,modifier=Modifier.background(color= MaterialTheme.colorScheme.surfaceVariant)
            ) {

                OutlinedTextField(

                    value = selectedPlaylist,

                    onValueChange = {},

                    readOnly = true,

                    label = {
                        Text("Playlist")
                    },

                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },

                    modifier = Modifier.menuAnchor()

                )

                ExposedDropdownMenu(

                    expanded = expanded,

                    onDismissRequest = {
                        expanded = false
                    },


                    ) {
                    playlists.forEach { location ->

                        DropdownMenuItem(

                            text = {
                                Text(location)
                            },

                            onClick = {

                                selectedPlaylist = location
                                expanded = false

                            }
                        )

                    }

                }

            }
        }
            item{Spacer(modifier.height(18.dp))}
            item{
                Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Top){
                    Button(onClick={}) {
                        Text("Post")
                    }
                    Spacer(modifier.width(10.dp))
                    Button(onClick={}) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}