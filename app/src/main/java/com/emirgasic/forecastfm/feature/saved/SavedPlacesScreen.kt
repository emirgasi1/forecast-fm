package com.emirgasic.forecastfm.feature.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.ui.components.place.PlaceCard
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import com.emirgasic.forecastfm.di.ServiceLocator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@Composable
fun SavedPlacesScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SavedPlacesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SavedPlacesViewModel(
                    placeRepository = ServiceLocator.providePlaceRepository()
                ) as T
            }
        }
    )
) {
    val savedPlaces by viewModel.savedPlaces.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // 👈 THEME BACKGROUND
            .padding(top = 60.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Back",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { navController.popBackStack() }
            )

            Text(
                text = "Saved Places",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            if (savedPlaces.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved places yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(savedPlaces) { place ->
                        PlaceCard(
                            name = place.name,
                            category = place.category,
                            openingHours = place.openingHours,
                            outdoorSeating = place.outdoorSeating,
                            onViewPlaceClick = {
                                // Make sure this route matches your NavGraph
                                navController.navigate("placeInfo/${place.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}