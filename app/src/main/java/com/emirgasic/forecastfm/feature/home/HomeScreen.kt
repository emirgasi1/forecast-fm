package com.emirgasic.forecastfm.feature.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import com.emirgasic.forecastfm.core.ui.components.common.ForecastItem
import com.emirgasic.forecastfm.core.ui.components.common.LoadingScreen
import com.emirgasic.forecastfm.core.ui.components.home.PlaylistCard
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.common.WeatherCard
import com.emirgasic.forecastfm.data.repository.HomeRepository
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import com.emirgasic.forecastfm.feature.map.LocationManager
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import kotlin.toString

@Composable
fun HomeScreen(
    mainNavController: NavController,
    rootNavController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val viewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(
                    tokenManager = tokenManager,
                    onboardingPrefs = OnboardingPreferences(context),
                    homeRepository = HomeRepository(),
                    locationRepository = LocationRepository(),
                    weatherRepository = WeatherRepository(),
                    playlistRepository = PlaylistRepository(PlaylistApi())
                ) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    val locationManager = remember { LocationManager(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.loadHome(null, null)
        }
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            val location = locationManager.getCurrentLocation()
            if (location != null) {
                viewModel.loadHome(location.latitude, location.longitude)
            } else {
                viewModel.loadHome(null, null)
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    when (val state = uiState) {

        HomeUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingScreen()
            }
        }

        is HomeUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = { viewModel.loadHome(null, null) }
                    ) {
                        Text("Retry")
                    }
                }
            }
        }

        is HomeUiState.Success -> {
            val home = state.home

            Box(
                modifier = modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
            ) {
                LazyColumn(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top,
                    modifier = modifier.fillMaxSize().padding(10.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = home.greeting,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Image(
                                painter = painterResource(home.weather.icon),
                                contentDescription = "${home.weather.condition} weather",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = home.weather.location,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        WeatherCard(
                            temperature = home.weather.temperature,
                            weather = home.weather.condition,
                            feelsLike = home.weather.feelsLike,
                            humidity = home.weather.humidity,
                            wind = home.weather.wind
                        )
                        Spacer(modifier.height(26.dp))
                        SectionTitle(title = "5-day Forecast")
                        Spacer(modifier.height(38.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
                                            MaterialTheme.colorScheme.background.copy(alpha = 0.55f),
                                            MaterialTheme.colorScheme.background
                                        )
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .clickable { rootNavController.navigate(Routes.Weather) }
                        ) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(26.dp)
                            ) {
                                items(home.forecast) { forecast ->
                                    ForecastItem(
                                        icon = forecast.icon,
                                        day = forecast.time,
                                        temperature = forecast.temperature
                                    )
                                }
                            }
                        }
                        Spacer(modifier.height(26.dp))
                        SectionTitle(title = "Today's Soundtrack")
                        Spacer(modifier = modifier.height(20.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            home.playlists.forEach { playlist ->
                                PlaylistCard(
                                    title = playlist.title,
                                    genre = playlist.genre,
                                    artwork = playlist.albumImageUrl,
                                    likes = playlist.likes.toString(),
                                    onClick = {
                                        rootNavController.navigate(
                                            Routes.playlistRoute(playlist.id)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}