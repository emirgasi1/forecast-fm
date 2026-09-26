package com.emirgasic.forecastfm.feature.place

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.common.LoadingScreen
import com.emirgasic.forecastfm.core.ui.components.common.WeatherReadingsGrid
import com.emirgasic.forecastfm.core.ui.components.place.PlaceInfoAbout
import com.emirgasic.forecastfm.core.ui.components.place.PlaceInfoActions
import com.emirgasic.forecastfm.core.ui.components.place.PlaceInfoHeader
import com.emirgasic.forecastfm.core.ui.components.place.PlaceInfoHero
import com.emirgasic.forecastfm.core.ui.components.place.PlaceInfoMapPreview
import com.emirgasic.forecastfm.data.model.WeatherReading
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun PlaceInfoScreen(
    navController: NavController,
    placeId: String?,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: PlaceInfoViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PlaceInfoViewModel(tokenManager) as T
            }
        }
    )
) {
    val place by viewModel.place.collectAsState()
    val locationName by viewModel.locationName.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val forecastColors = LocalForecastColors.current

    LaunchedEffect(placeId) {
        placeId?.let { viewModel.loadPlace(it) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading) {
            LoadingScreen()
        } else if (place == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Place not found: $placeId",
                    color = forecastColors.error
                )
            }
        } else {
            place?.let { item ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(
                                top = 60.dp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 24.dp
                            )
                    ) {
                        PlaceInfoHeader(
                            name = item.name,
                            category = item.category,
                            rating = item.rating,
                            isSaved = isSaved,
                            onBackClick = { navController.popBackStack() },
                            onSaveClick = { viewModel.toggleSave() }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        PlaceInfoHero(
                            name = item.name,
                            category = item.category,
                            rating = item.rating,
                            locationName = locationName
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        PlaceInfoMapPreview(
                            latitude = item.latitude,
                            longitude = item.longitude,
                            onClick = {
                                navController.navigate(Routes.FullMap)
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        PlaceInfoAbout(
                            description = item.description
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        WeatherReadingsGrid(
                            readings = listOf(
                                WeatherReading(
                                    label = "Rating",
                                    value = item.rating.toString(),
                                    icon = Icons.Default.Star
                                ),
                                WeatherReading(
                                    label = "Category",
                                    value = item.category,
                                    icon = Icons.Default.Category
                                ),
                                WeatherReading(
                                    label = "Address",
                                    value = item.address.ifBlank { "Unknown" },
                                    icon = Icons.Default.LocationOn
                                ),
                                WeatherReading(
                                    label = "Coordinates",
                                    value = "${"%.4f".format(item.latitude)}, ${"%.4f".format(item.longitude)}",
                                    icon = Icons.Default.Place
                                )
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(forecastColors.surface)
                            .border(
                                width = 1.dp,
                                color = forecastColors.border
                            )
                            .navigationBarsPadding()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 12.dp,
                                bottom = 16.dp
                            )
                    ) {
                        PlaceInfoActions(
                            name = item.name,
                            latitude = item.latitude,
                            longitude = item.longitude,
                            onGetDirectionsClick = {
                                navController.navigate(
                                    Routes.routeRoute(
                                        destLat = item.latitude,
                                        destLng = item.longitude,
                                        originLat = 0.0,
                                        originLng = 0.0
                                    )
                                )
                            },
                            onShowOnMapClick = {
                                navController.navigate(Routes.FullMap)
                            }
                        )
                    }
                }
            }
        }
    }
}