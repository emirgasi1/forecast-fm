package com.emirgasic.forecastfm.feature.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.theme.AppTheme
import com.emirgasic.forecastfm.core.theme.ThemeManager
import com.emirgasic.forecastfm.core.ui.components.map.MapBottomSheet
import com.emirgasic.forecastfm.core.ui.components.map.MapBottomSheetTabs
import com.emirgasic.forecastfm.core.ui.components.map.MapTab
import com.emirgasic.forecastfm.core.ui.components.map.tabs.LayersTabContent
import com.emirgasic.forecastfm.core.ui.components.map.tabs.LegendTabContent
import com.emirgasic.forecastfm.core.ui.components.map.tabs.PlaceDetailSlider
import com.emirgasic.forecastfm.core.ui.components.map.tabs.SearchTabContent
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.repository.BusStationRepository
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import com.emirgasic.forecastfm.di.ServiceLocator
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullMapScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    searchViewModel: PlaceSearchViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PlaceSearchViewModel(repository = PlaceRepository()) as T
            }
        }
    )
) {
    val context = LocalContext.current
    val viewModel: FullMapViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return FullMapViewModel(
                    locationRepository = ServiceLocator.provideLocationRepository(),
                    placeRepository = PlaceRepository(),
                    busStationRepository = BusStationRepository()
                ) as T
            }
        }
    )

    var userLocation by remember { mutableStateOf<Location?>(null) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationManager = remember { LocationManager(context) }
    val locations by viewModel.locations.collectAsState()
    val places by viewModel.places.collectAsState()
    val enabledLayers by viewModel.enabledLayers.collectAsState()
    val markerGeoJson by viewModel.markerGeoJson.collectAsState()

    val searchQuery by searchViewModel.query.collectAsState()
    val searchResults by searchViewModel.results.collectAsState()
    val isSearching by searchViewModel.isLoading.collectAsState()
    val searchError by searchViewModel.error.collectAsState()

    var selectedTab by remember { mutableStateOf(MapTab.Search) }
    var selectedSearchResult by remember { mutableStateOf<Place?>(null) }

    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(target = Position(latitude = 43.8563, longitude = 18.4131), zoom = 13.0)
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        viewModel.loadPlaces()
        viewModel.loadBusStations()
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val location = locationManager.getCurrentLocation()
            location?.let {
                userLocation = it
                cameraState.position = CameraPosition(target = Position(latitude = it.latitude, longitude = it.longitude), zoom = 14.0)
            }
        }
    }

    val selectedTheme by ThemeManager.selectedTheme.collectAsState()
    val styleJson = remember(selectedTheme) {
        val resolved = if (selectedTheme == AppTheme.AUTO) ThemeManager.resolveTheme() else selectedTheme
        val resId = when (resolved) {
            AppTheme.MORNING -> R.raw.map_style_morning
            AppTheme.AFTERNOON -> R.raw.map_style_afternoon
            AppTheme.NIGHT -> R.raw.map_style_night
            AppTheme.AUTO -> R.raw.map_style_morning
        }
        context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            baseStyle = BaseStyle.Json(styleJson)
        ) {
            // User location
            userLocation?.let { location ->
                val locationJson = """
            {"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[${location.longitude},${location.latitude}]},"properties":{}}]}
        """.trimIndent()

                CircleLayer(
                    id = "user-location-marker",
                    source = rememberGeoJsonSource(data = GeoJsonData.JsonString(locationJson)),
                    radius = const(8.dp),
                    color = const(Color(0xFF2196F3)),
                    strokeWidth = const(3.dp),
                    strokeColor = const(Color.White),
                    strokeOpacity = const(1f)
                )
            }

            // Single layer for all markers
            key(markerGeoJson) {
                MarkersLayer(
                    jsonString = markerGeoJson,
                    locations = locations,
                    places = places,
                    viewModel = viewModel
                )
            }
        }

        Text(
            text = "Back",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                .clickable { navController.popBackStack() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)).clickable {
                    cameraState.position = CameraPosition(target = cameraState.position.target, zoom = cameraState.position.zoom, bearing = 0.0, tilt = 0.0)
                },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Explore, contentDescription = "Compass", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            }
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)).clickable {
                    cameraState.position = CameraPosition(target = cameraState.position.target, zoom = (cameraState.position.zoom + 1.0).coerceAtMost(22.0), bearing = cameraState.position.bearing, tilt = cameraState.position.tilt)
                },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom in", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            }
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)).clickable {
                    cameraState.position = CameraPosition(target = cameraState.position.target, zoom = (cameraState.position.zoom - 1.0).coerceAtLeast(1.0), bearing = cameraState.position.bearing, tilt = cameraState.position.tilt)
                },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom out", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            }
        }

        MapBottomSheet(
            modifier = Modifier.align(Alignment.BottomCenter),
            content = {
                val detail = selectedSearchResult
                if (detail != null) {
                    PlaceDetailSlider(
                        place = detail,
                        onGetDirectionsClick = {
                            val originLat = userLocation?.latitude ?: 0.0
                            val originLng = userLocation?.longitude ?: 0.0
                            navController.navigate(Routes.routeRoute(destLat = detail.latitude, destLng = detail.longitude, originLat = originLat, originLng = originLng))
                        }
                    )
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        MapBottomSheetTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
                        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                            when (selectedTab) {
                                MapTab.Search -> SearchTabContent(query = searchQuery, onQueryChange = { searchViewModel.updateQuery(it) }, results = searchResults, isLoading = isSearching, error = searchError, onPlaceClick = { place -> selectedSearchResult = place })
                                MapTab.Legend -> LegendTabContent()
                                MapTab.Layers -> LayersTabContent(enabledLayers = enabledLayers, onLayerToggle = { viewModel.toggleLayer(it) })
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        )
    }
}