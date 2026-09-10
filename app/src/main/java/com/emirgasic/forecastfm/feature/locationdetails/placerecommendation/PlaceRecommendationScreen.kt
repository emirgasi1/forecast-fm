package com.emirgasic.forecastfm.feature.locationdetails.placerecommendation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.locationdetails.locationrecommendation.AgeGroupSelector
import com.emirgasic.forecastfm.core.ui.components.locationdetails.locationrecommendation.CompanionSelector
import com.emirgasic.forecastfm.core.ui.components.locationdetails.locationrecommendation.PlaceCategorySelector
import com.emirgasic.forecastfm.core.ui.components.locationdetails.locationrecommendation.PlaceRecommendationCard
import com.emirgasic.forecastfm.core.ui.components.locationdetails.locationrecommendation.WeatherSelector

@Composable
fun PlaceRecommendationScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PlaceRecommendationViewModel = viewModel()
) {
    val recommendations by viewModel.recommendations.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedCompanion by viewModel.selectedCompanion.collectAsState()
    val selectedWeather by viewModel.selectedWeather.collectAsState()
    val selectedAgeGroup by viewModel.selectedAgeGroup.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Find a place",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    Text(
                        text = "What are you looking for?",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    PlaceCategorySelector(
                        categories = listOf("Coffee", "Restaurant", "Shop", "Activity", "Nightlife", "Culture"),
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.selectCategory(it) }
                    )
                }

                item {
                    Text(
                        text = "Who are you with?",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    CompanionSelector(
                        companions = listOf("Alone", "Partner", "Family", "Friends", "Colleagues"),
                        selectedCompanion = selectedCompanion,
                        onCompanionSelected = { viewModel.selectCompanion(it) }
                    )
                }

                item {
                    Text(
                        text = "Weather",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    WeatherSelector(
                        weathers = listOf("Sunny", "Rainy", "Cloudy", "Snowy", "Clear", "Any"),
                        selectedWeather = selectedWeather,
                        onWeatherSelected = { viewModel.selectWeather(it) }
                    )
                }

                item {
                    Text(
                        text = "Age Group",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    AgeGroupSelector(
                        ageGroups = listOf("Teens", "Young Adults", "Adults", "Seniors", "All Ages"),
                        selectedAgeGroup = selectedAgeGroup,
                        onAgeGroupSelected = { viewModel.selectAgeGroup(it) }
                    )
                }

                item {
                    Text(
                        text = "Recommended places (${recommendations.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(recommendations) { place ->
                    PlaceRecommendationCard(
                        name = place.name,
                        category = place.category,
                        location = place.location,
                        description = place.description,
                        imageUrl = place.imageUrl,
                        rating = place.rating,
                        onClick = {
                            navController.navigate(Routes.placeRecommendationDetailRoute(place.id))
                        }
                    )
                }
            }
        }
    }
}