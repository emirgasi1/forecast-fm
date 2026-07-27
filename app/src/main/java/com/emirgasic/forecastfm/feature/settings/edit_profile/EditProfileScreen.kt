package com.emirgasic.forecastfm.feature.settings.edit_profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController,modifier: Modifier =Modifier){

    var username by rememberSaveable {
        mutableStateOf("")
    }
    var bio by rememberSaveable {
        mutableStateOf("")
    }
    val locations = listOf(
        "Baščaršija",
        "Ilidža",
        "Trebević",
        "Dobrinja"
    )
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedLocation by rememberSaveable {
        mutableStateOf("")
    }
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 60.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    ){
        Text(text="Edit Profile",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineMedium)

        LazyColumn(modifier=Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally){


            item{
                Spacer(modifier.height(54.dp))
            }
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
                }
            }
            item{
                Spacer(modifier.height(12.dp))
            }
            item{
                Text(text="Change Photo",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineSmall)
            }
            item{
                Spacer(modifier.height(28.dp))
            }
            item{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ){
                    Text(text="Username",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineSmall)
                }
            }
            item{
                Spacer(modifier.height(8.dp))
            }
            item{
                OutlinedTextField(
                    value=username,
                    onValueChange={username=it},
                    modifier.fillMaxWidth(),
                    label={
                        Text("Emir")
                    }, singleLine = true
                )
            }
            item{
                Spacer(modifier.height(28.dp))
            }
            item{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ){
                    Text(text="Bio",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineSmall)
                }
            }
            item{
                Spacer(modifier.height(8.dp))
            }
            item{
                OutlinedTextField(
                    value=bio,
                    onValueChange={bio=it},
                    modifier.fillMaxWidth()
                        .height(120.dp),
                    label={
                        Text("Coffee. Music. Sarajevo.")
                    }
                )
            }
            item{
                Spacer(modifier.height(28.dp))
            }

            item{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ){
                    Text(text="Favorite Location",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineSmall)
                }
            }
            item{
                Spacer(modifier.height(8.dp))
            }
            item{

                ExposedDropdownMenuBox(

                    expanded = expanded,

                    onExpandedChange = {
                        expanded = !expanded
                    }

                ) {

                    OutlinedTextField(

                        value = selectedLocation,

                        onValueChange = {},

                        readOnly = true,

                        label = {
                            Text("Location")
                        },

                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()

                    )


                    ExposedDropdownMenu(

                        expanded = expanded,

                        onDismissRequest = {
                            expanded = false
                        }

                    ) {

                        locations.forEach { location ->

                            DropdownMenuItem(

                                text = {
                                    Text(location)
                                },

                                onClick = {

                                    selectedLocation = location
                                    expanded = false

                                }

                            )

                        }

                    }

                }

            }
            item{
                Spacer(modifier.height(20.dp))
            }
            item{
                Button(onClick = {}) {
                    Text(text="Save")
                }
            }
        }
    }
}
