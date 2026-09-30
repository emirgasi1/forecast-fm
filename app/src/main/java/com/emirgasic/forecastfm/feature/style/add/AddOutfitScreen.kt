package com.emirgasic.forecastfm.feature.style.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.network.outfit.OutfitApi
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOutfitScreen(
    navController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: AddOutfitViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AddOutfitViewModel(
                    tokenManager = tokenManager,
                    outfitApi = OutfitApi()
                ) as T
            }
        }
    )
) {
    val context = LocalContext.current
    val outfitApi = remember { OutfitApi() }
    val state by viewModel.state.collectAsState()

    var weatherExpanded by rememberSaveable { mutableStateOf(false) }
    var seasonExpanded by rememberSaveable { mutableStateOf(false) }

    val weatherOptions = listOf("Clear", "Clouds", "Rain", "Drizzle", "Snow", "Thunderstorm")
    val seasonOptions = listOf("Spring", "Summer", "Autumn", "Winter")

    val forecastColors = LocalForecastColors.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.setImage(it)
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = forecastColors.card,
        unfocusedContainerColor = forecastColors.card,
        focusedTextColor = forecastColors.title,
        unfocusedTextColor = forecastColors.title,
        focusedBorderColor = forecastColors.primary,
        unfocusedBorderColor = forecastColors.border,
        focusedLabelColor = forecastColors.muted,
        unfocusedLabelColor = forecastColors.muted,
        focusedPlaceholderColor = forecastColors.muted,
        unfocusedPlaceholderColor = forecastColors.muted,
        cursorColor = forecastColors.primary
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 60.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            Text(
                text = "← Back",
                style = MaterialTheme.typography.titleMedium,
                color = forecastColors.title,
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .clickable { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add Outfit",
                style = MaterialTheme.typography.headlineMedium,
                color = forecastColors.title
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .shadow(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = forecastColors.shadow.copy(alpha = 0.25f),
                        spotColor = forecastColors.shadow.copy(alpha = 0.4f)
                    )
                    .clickable {
                        imagePicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, forecastColors.border),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = forecastColors.card
                )
            ) {
                if (state.imageUri != null) {
                    AsyncImage(
                        model = state.imageUri,
                        contentDescription = "Selected outfit image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tap to select an image",
                            color = forecastColors.muted,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.setTitle(it) },
                label = { Text("Title") },
                placeholder = { Text("e.g. Sunny City Casual") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = weatherExpanded,
                onExpandedChange = { weatherExpanded = !weatherExpanded }
            ) {
                OutlinedTextField(
                    value = state.weatherCondition,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Weather condition") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = weatherExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = fieldColors
                )

                ExposedDropdownMenu(
                    expanded = weatherExpanded,
                    onDismissRequest = { weatherExpanded = false },
                    modifier = Modifier.background(forecastColors.card)
                ) {
                    weatherOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(option, color = forecastColors.title)
                            },
                            onClick = {
                                viewModel.setWeatherCondition(option)
                                weatherExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = seasonExpanded,
                onExpandedChange = { seasonExpanded = !seasonExpanded }
            ) {
                OutlinedTextField(
                    value = state.season,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Season") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = seasonExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = fieldColors
                )

                ExposedDropdownMenu(
                    expanded = seasonExpanded,
                    onDismissRequest = { seasonExpanded = false },
                    modifier = Modifier.background(forecastColors.card)
                ) {
                    seasonOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(option, color = forecastColors.title)
                            },
                            onClick = {
                                viewModel.setSeason(option)
                                seasonExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.storeName,
                onValueChange = { viewModel.setStoreName(it) },
                label = { Text("Store (optional)") },
                placeholder = { Text("e.g. Zara") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.storeAddress,
                onValueChange = { viewModel.setStoreAddress(it) },
                label = { Text("Store address (optional)") },
                placeholder = { Text("e.g. Sarajevo City Center") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.price,
                onValueChange = { viewModel.setPrice(it) },
                label = { Text("Price range (optional)") },
                placeholder = { Text("e.g. 60-100 KM") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.storePhone,
                onValueChange = { viewModel.setStorePhone(it) },
                label = { Text("Store phone (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.productUrl,
                onValueChange = { viewModel.setProductUrl(it) },
                label = { Text("Product link (optional)") },
                placeholder = { Text("https://...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            if (state.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.error ?: "",
                    color = forecastColors.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.submit(
                        uploadImage = { uri ->
                            outfitApi.uploadOutfitImage(
                                contentResolver = context.contentResolver,
                                imageUri = uri.toString()
                            )
                        },
                        onSuccess = {
                            navController.popBackStack()
                        }
                    )
                },
                enabled = state.canSubmit && !state.isUploading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = forecastColors.primary,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                if (state.isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        color = MaterialTheme.colorScheme.background
                    )
                } else {
                    Text(
                        text = "Add Outfit",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}