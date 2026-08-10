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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.ui.components.common.ScreenTitle
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.editprofile.ProfilePhotoEditor
import com.emirgasic.forecastfm.core.ui.components.editprofile.ProfileTextField
import com.emirgasic.forecastfm.core.ui.components.map.LocationDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = viewModel()
) {

    val profile by viewModel.profile.collectAsState()


    var expanded by rememberSaveable {
        mutableStateOf(false)
    }


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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            item {
                ScreenTitle(
                    title = "Edit Profile"
                )
            }



            item {
                Spacer(
                    modifier = Modifier.height(54.dp)
                )
            }



            item {
                ProfilePhotoEditor(
                    image = painterResource(profile.profileImage),
                    onClick = {
                        // Later: open image picker
                    }
                )
            }



            item {
                Spacer(
                    modifier = Modifier.height(28.dp)
                )
            }



            item {
                ProfileTextField(
                    title = "Username",
                    value = profile.username,
                    onValueChange = {
                        viewModel.updateUsername(it)
                    },
                    placeholder = "Emir"

                )
            }



            item {
                Spacer(
                    modifier = Modifier.height(28.dp)
                )
            }



            item {
                ProfileTextField(
                    title = "Bio",
                    value = profile.bio,
                    onValueChange = {
                        viewModel.updateBio(it)
                    },
                    placeholder = "Coffee. Music. Sarajevo.",
                    singleLine = false,
                    height = 120.dp
                )

            }



            item {
                Spacer(
                    modifier = Modifier.height(28.dp)
                )
            }



            item {
                SectionTitle(
                    title = "Favorite Location"
                )
            }



            item {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }



            item {
                LocationDropdown(

                    selectedLocation = profile.favoriteLocation,
                    locations = profile.locations,
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = it
                    },
                    onLocationSelected = {
                        viewModel.updateLocation(it)
                        expanded = false
                    }
                )
            }



            item {
                Spacer(
                    modifier = Modifier.height(20.dp)
                )
            }



            item {
                Button(
                    onClick = {
                    }
                ) {
                    Text(
                        text = "Save"
                    )
                }

            }

        }

    }

}