package com.emirgasic.forecastfm.feature.style

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.common.LoadingScreen
import com.emirgasic.forecastfm.core.ui.components.common.ScreenTitle
import com.emirgasic.forecastfm.core.ui.components.style.HeroOutfitCard
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.SavedOutfitRepository
import com.emirgasic.forecastfm.di.ServiceLocator
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun StyleScreen(
    navController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: StyleViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return StyleViewModel(
                    tokenManager = tokenManager,
                    outfitRepository = ServiceLocator.provideOutfitRepository(),
                    savedOutfitRepository = ServiceLocator.provideSavedOutfitRepository(),
                    weatherCacheManager = ServiceLocator.provideWeatherCacheManager()
                ) as T
            }
        }
    )
) {
    val style by viewModel.style.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val savedOutfitIds by viewModel.savedOutfitIds.collectAsState()
    val currentCondition by viewModel.currentCondition.collectAsState()

    val forecastColors = LocalForecastColors.current

    val shouldRefresh by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("refresh_style", false)
        ?.collectAsState() ?: remember { mutableStateOf(false) }

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            navController.currentBackStackEntry?.savedStateHandle?.set("refresh_style", false)
        }
        viewModel.loadStyle()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                .zIndex(1f), // Keeps it above the scrolling cards
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScreenTitle(title = "Style")

            IconButton(
                onClick = { navController.navigate(Routes.AddOutfit) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add outfit",
                    tint = forecastColors.primary
                )
            }
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingScreen()
                }
            }
            error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Couldn't load outfits", color = forecastColors.title, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(error ?: "", color = forecastColors.muted, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadStyle() }) { Text("Retry") }
                }
            }
            style != null -> {
                val allOutfits = style!!.outfits

                if (allOutfits.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No outfits available", color = forecastColors.muted)
                    }
                } else {

                    val currentBucketOutfits = allOutfits.filter {
                        com.emirgasic.forecastfm.data.model.WeatherCondition.from(it.weatherCondition).bucket == currentCondition.bucket
                    }
                    val otherOutfits = allOutfits.filter {
                        com.emirgasic.forecastfm.data.model.WeatherCondition.from(it.weatherCondition).bucket != currentCondition.bucket
                    }

                    val sortedOutfits = currentBucketOutfits + otherOutfits

                    val pagerState = rememberPagerState(pageCount = { sortedOutfits.size })

                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 80.dp, bottom = 24.dp),
                        pageSpacing = 16.dp
                    ) { page ->
                        val outfit = sortedOutfits[page]
                        val isSaved = outfit.id in savedOutfitIds

                        HeroOutfitCard(
                            imageUrl = outfit.imageUrl,
                            title = outfit.title,
                            weatherCondition = outfit.weatherCondition,
                            season = outfit.season,
                            likes = outfit.likes,
                            storeName = outfit.storeName,
                            storeAddress = outfit.storeAddress,
                            price = outfit.price,
                            isSaved = isSaved,
                            onLikeClick = { viewModel.toggleSaveOutfit(outfit.id) },
                            onClick = { navController.navigate(Routes.styleDetailRoute(outfit.id)) },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}