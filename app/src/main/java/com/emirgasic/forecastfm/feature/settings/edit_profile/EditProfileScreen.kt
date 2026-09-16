package com.emirgasic.forecastfm.feature.settings.edit_profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.ui.components.common.ScreenTitle
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.editprofile.ProfilePhotoEditor
import com.emirgasic.forecastfm.core.ui.components.editprofile.ProfileTextField
import com.emirgasic.forecastfm.core.ui.components.map.LocationDropdown
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return EditProfileViewModel(tokenManager) as T
            }
        }
    )
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var expanded by rememberSaveable { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.uploadImage(context.contentResolver, it.toString())
        }
    }

    LaunchedEffect(state.savedMessage) {
        if (state.savedMessage != null) {
            delay(2000)
            viewModel.clearMessages()
        }
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
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    ScreenTitle(title = "Edit Profile")
                }

                item {
                    Spacer(modifier = Modifier.height(54.dp))
                }

                item {
                    ProfilePhotoEditor(
                        image = state.profileImageUrl,
                        onClick = {
                            imagePicker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(28.dp))
                }

                item {
                    ProfileTextField(
                        title = "Username",
                        value = state.username,
                        onValueChange = { viewModel.updateUsername(it) },
                        placeholder = "Emir"
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(28.dp))
                }

                item {
                    ProfileTextField(
                        title = "Bio",
                        value = state.bio,
                        onValueChange = { viewModel.updateBio(it) },
                        placeholder = "Coffee. Music. Sarajevo.",
                        singleLine = false,
                        height = 120.dp
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(28.dp))
                }

                item {
                    SectionTitle(title = "Favorite Location")
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    LocationDropdown(
                        selectedLocation = state.favoriteLocation,
                        locations = state.locations,
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        onLocationSelected = {
                            viewModel.updateLocation(it)
                            expanded = false
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    Button(
                        onClick = { viewModel.saveProfile() },
                        enabled = !state.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Text(
                            text = if (state.isSaving) "Saving..." else "Save",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                state.savedMessage?.let { msg ->
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                state.error?.let { err ->
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}