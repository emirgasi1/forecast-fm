package com.emirgasic.forecastfm.feature.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import com.emirgasic.forecastfm.core.theme.AppTheme
import com.emirgasic.forecastfm.core.theme.ThemeManager
import com.emirgasic.forecastfm.core.ui.components.common.ScreenTitle
import com.emirgasic.forecastfm.core.ui.components.map.ExpandMapButton
import com.emirgasic.forecastfm.core.ui.components.map.LocationDropdown
import com.emirgasic.forecastfm.core.ui.components.map.LocationRecommendationCard
import com.emirgasic.forecastfm.core.ui.components.place.PlaceCard
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.repository.BusStationRepository
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import com.emirgasic.forecastfm.feature.weather.WeatherViewModel
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Position

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mainNavController: NavController,
    rootNavController: NavController,
    modifier: Modifier = Modifier,
    searchViewModel: PlaceSearchViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PlaceSearchViewModel(
                    repository = PlaceRepository()
                ) as T
            }
        }
    ),
    weatherViewModel: WeatherViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WeatherViewModel(
                    repository = WeatherRepository()
                ) as T
            }
        }
    )
) {
    val context = LocalContext.current

    val viewModel: MapViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MapViewModel(
                    onboardingPrefs = OnboardingPreferences(context),
                    locationRepository = LocationRepository(),
                    outfitRepository = OutfitRepository(),
                    placeRepository = PlaceRepository(),
                    playlistRepository = PlaylistRepository(
                        playlistApi = PlaylistApi()
                    ),
                    busStationRepository = BusStationRepository()
                ) as T
            }
        }
    )

    var userLocation by remember { mutableStateOf<Location?>(null) }
    val weather by weatherViewModel.weather.collectAsState()
    val selectedTheme by ThemeManager.selectedTheme.collectAsState()

    val styleJson = remember(selectedTheme) {
        val resolved = if (selectedTheme == AppTheme.AUTO) {
            ThemeManager.resolveTheme()
        } else {
            selectedTheme
        }

        val resId = when (resolved) {
            AppTheme.MORNING -> R.raw.map_style_morning
            AppTheme.AFTERNOON -> R.raw.map_style_afternoon
            AppTheme.NIGHT -> R.raw.map_style_night
            AppTheme.AUTO -> R.raw.map_style_morning
        }

        context.resources.openRawResource(resId)
            .bufferedReader()
            .use { it.readText() }
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationManager = remember {
        LocationManager(context)
    }

    val outfits by viewModel.outfits.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val places by viewModel.visiblePlaces.collectAsState()
    val selectedPlace by viewModel.selectedPlace.collectAsState()
    val markers by viewModel.filteredMarkers.collectAsState()
    val playlistMap by viewModel.playlistMap.collectAsState()
    val venueFilter by viewModel.venueCategoryFilter.collectAsState()

    val searchQuery by searchViewModel.query.collectAsState()
    val searchResults by searchViewModel.results.collectAsState()
    val isSearching by searchViewModel.isLoading.collectAsState()
    val searchError by searchViewModel.error.collectAsState()

    var selectedSearchResult by remember { mutableStateOf<Place?>(null) }

    var expanded by remember { mutableStateOf(false) }
    var placesExpanded by remember { mutableStateOf(false) }

    val locationNames = locations.map { it.name }
    val placeNames = places.map { it.name }

    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(
                latitude = 43.8563,
                longitude = 18.4131
            ),
            zoom = 12.0
        )
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { location ->
            weatherViewModel.loadWeather(
                location = location.name,
                latitude = location.latitude,
                longitude = location.longitude
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadLocations()
        viewModel.loadAllPlaces()
        viewModel.loadBusStations()

        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val location = locationManager.getCurrentLocation()

            location?.let {
                if (it.latitude != 0.0 && it.longitude != 0.0) {
                    userLocation = it

                    cameraState.position = CameraPosition(
                        target = Position(
                            latitude = it.latitude,
                            longitude = it.longitude
                        ),
                        zoom = 14.0
                    )
                }
            }
        }
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { location ->
            cameraState.animateTo(
                CameraPosition(
                    target = Position(
                        longitude = location.longitude,
                        latitude = location.latitude
                    ),
                    zoom = 14.0
                )
            )
        }
    }

    LaunchedEffect(weather, selectedLocation) {
        val condition = weather?.condition ?: return@LaunchedEffect
        val location = selectedLocation ?: return@LaunchedEffect

        viewModel.loadOutfits(condition)
        viewModel.loadPlaylistForLocation(
            location.id,
            condition
        )
    }

    LaunchedEffect(selectedPlace) {
        selectedPlace?.let { place ->
            cameraState.animateTo(
                CameraPosition(
                    target = Position(
                        longitude = place.longitude,
                        latitude = place.latitude
                    ),
                    zoom = 15.0
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 20.dp,
                start = 10.dp,
                bottom = 10.dp,
                end = 10.dp
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScreenTitle(
                    title = "Map",
                )

                IconButton(
                    onClick = {
                        userLocation?.let { location ->
                            cameraState.position = CameraPosition(
                                target = Position(
                                    longitude = location.longitude,
                                    latitude = location.latitude
                                ),
                                zoom = 14.0
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "My location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                MaplibreMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    cameraState = cameraState,
                    baseStyle = BaseStyle.Json(styleJson)
                ) {
                    userLocation?.let { location ->
                        key(location.latitude, location.longitude) {
                            val locationJson = """
                                {
                                    "type": "FeatureCollection",
                                    "features": [
                                        {
                                            "type": "Feature",
                                            "geometry": {
                                                "type": "Point",
                                                "coordinates": [
                                                    ${location.longitude},
                                                    ${location.latitude}
                                                ]
                                            },
                                            "properties": {}
                                        }
                                    ]
                                }
                            """.trimIndent()

                            val userLocationSource = rememberGeoJsonSource(
                                data = GeoJsonData.JsonString(locationJson)
                            )

                            CircleLayer(
                                id = "user-location-marker",
                                source = userLocationSource,
                                radius = const(8.dp),
                                color = const(Color.Blue),
                                strokeWidth = const(3.dp),
                                strokeColor = const(Color.White),
                                strokeOpacity = const(1f)
                            )
                        }
                    }

                    markers.forEach { marker ->
                        key(marker.id) {
                            val markerJson = """
                                {
                                    "type": "FeatureCollection",
                                    "features": [
                                        {
                                            "type": "Feature",
                                            "geometry": {
                                                "type": "Point",
                                                "coordinates": [
                                                    ${marker.longitude},
                                                    ${marker.latitude}
                                                ]
                                            },
                                            "properties": {
                                                "id": "${marker.id}",
                                                "name": "${marker.name}",
                                                "type": "${marker.type}"
                                            }
                                        }
                                    ]
                                }
                            """.trimIndent()

                            val markerSource = rememberGeoJsonSource(
                                data = GeoJsonData.JsonString(markerJson)
                            )

                            val markerColor = when {
                                marker.isSelected && marker.type == "venue" -> Color(0xFFE53935)  // red — selected venue
                                marker.isSelected && marker.type == "place" -> Color(0xFF43A047)  // green — selected place
                                else -> Color(0xFF9C27B0)                                         // purple — unselected
                            }

                            CircleLayer(
                                id = "marker-${marker.id}",
                                source = markerSource,
                                radius = const(
                                    if (marker.isSelected) {
                                        12.dp
                                    } else {
                                        9.dp
                                    }
                                ),
                                color = const(markerColor),
                                strokeWidth = const(3.dp),
                                strokeColor = const(Color.White),
                                strokeOpacity = const(1f),
                                onClick = { features ->
                                    val markerName = features
                                        .firstOrNull()
                                        ?.properties
                                        ?.get("name")
                                        ?.toString()
                                        ?.removeSurrounding("\"")

                                    if (markerName != null) {
                                        locations
                                            .firstOrNull {
                                                it.name == markerName
                                            }
                                            ?.let {
                                                viewModel.selectLocation(it)
                                            }
                                            ?: places
                                                .firstOrNull {
                                                    it.name == markerName
                                                }
                                                ?.let {
                                                    viewModel.selectPlace(it)
                                                }
                                    }

                                    ClickResult.Pass
                                }
                            )
                        }
                    }
                }

                ExpandMapButton(
                    icon = Icons.Default.Fullscreen,
                    contentDescription = "Expand map",
                    onClick = {
                        rootNavController.navigate(Routes.FullMap)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    null to "All",
                    "Cafe" to "Cafe",
                    "Restaurant" to "Restaurant",
                    "Shop" to "Shop",
                    "Nightlife" to "Nightlife"
                ).forEach { (value, label) ->
                    FilterChip(
                        selected = venueFilter == value,
                        onClick = {
                            viewModel.setVenueCategoryFilter(value)
                        },
                        label = {
                            Text(label)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.background,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.background,
                            selectedTrailingIconColor = MaterialTheme.colorScheme.background,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LocationDropdown(
                selectedLocation = selectedLocation?.name ?: "",
                locations = locationNames,
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                },
                onLocationSelected = { name ->
                    locations
                        .firstOrNull { it.name == name }
                        ?.let {
                            viewModel.selectLocation(it)
                        }

                    expanded = false
                }
            )

            if (places.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                LocationDropdown(
                    selectedLocation = selectedPlace?.name ?: "",
                    locations = placeNames,
                    expanded = placesExpanded,
                    onExpandedChange = {
                        placesExpanded = it
                    },
                    onLocationSelected = { name ->
                        places
                            .firstOrNull { it.name == name }
                            ?.let {
                                viewModel.selectPlace(it)
                            }

                        placesExpanded = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            selectedLocation?.let { location ->
                weather?.let { weatherData ->
                    val outfitName = outfits.firstOrNull()?.title
                        ?: "Recommended Outfit"

                    val playlistName = playlistMap[location.id]
                        ?: "Today's Soundtrack"

                    LocationRecommendationCard(
                        location = location.name,
                        weatherIcon = painterResource(weatherData.icon),
                        temperature = weatherData.temperature,
                        weather = weatherData.condition,
                        music = playlistName,
                        outfit = outfitName,
                        onViewDetailsClick = {
                            rootNavController.navigate(
                                Routes.locationDetailsRoute(location.id)
                            )
                        }
                    )
                }
            }

            selectedPlace?.let { place ->
                Spacer(modifier = Modifier.height(12.dp))

                PlaceCard(
                    name = place.name,
                    category = place.category,
                    rating = place.rating,
                    onViewPlaceClick = {
                        rootNavController.navigate(
                            Routes.placeInfoRoute(place.id)
                        )
                    }
                )
            }
        }
    }
}