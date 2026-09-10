package com.emirgasic.forecastfm.feature.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.common.ScreenTitle
import com.emirgasic.forecastfm.core.ui.components.map.LocationDropdown
import com.emirgasic.forecastfm.core.ui.components.map.LocationRecommendationCard
import com.emirgasic.forecastfm.core.ui.components.place.PlaceCard
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.feature.weather.WeatherViewModel
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
    viewModel: MapViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel()
) {
    var userLocation by remember {
        mutableStateOf<Location?>(null)
    }
    val context = LocalContext.current
    val weather by weatherViewModel.weather.collectAsState()

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
    val places by viewModel.places.collectAsState()
    val selectedPlace by viewModel.selectedPlace.collectAsState()
    val markers by viewModel.markers.collectAsState()
    val playlistMap by viewModel.playlistMap.collectAsState()

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

    LaunchedEffect(weather) {
        weather?.let {
            viewModel.loadOutfits(it.condition)
            selectedLocation?.let { location ->
                viewModel.loadPlaylistForLocation(location.id, it.condition)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 20.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
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
                    icon = painterResource(R.drawable.mappin)
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

            Spacer(modifier.height(16.dp))

            MaplibreMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                cameraState = cameraState,
                baseStyle = BaseStyle.Uri(
                    "https://tiles.openfreemap.org/styles/liberty"
                )
            ) {
                userLocation?.let { location ->
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

                // Individual markers for venues and places
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

                        val markerColor = if (marker.isSelected) {
                            Color(0xFF4CAF50) // Green for selected
                        } else {
                            Color(0xFF9C27B0) // Purple for unselected
                        }

                        CircleLayer(
                            id = "marker-${marker.id}",
                            source = markerSource,
                            radius = const(if (marker.isSelected) 12.dp else 9.dp),
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
                                    locations.firstOrNull { it.name == markerName }?.let {
                                        viewModel.selectLocation(it)
                                    } ?: places.firstOrNull { it.name == markerName }?.let {
                                        viewModel.selectPlace(it)
                                    }
                                }
                                ClickResult.Pass
                            }
                        )
                    }
                }
            }

            Spacer(modifier.height(16.dp))

            // Location Dropdown (Venues)
            LocationDropdown(
                selectedLocation = selectedLocation?.name ?: "",
                locations = locationNames,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onLocationSelected = { name ->
                    locations.firstOrNull { it.name == name }?.let {
                        viewModel.selectLocation(it)
                    }
                    expanded = false
                }
            )

            // Places Dropdown
            if (places.isNotEmpty()) {
                Spacer(modifier.height(12.dp))
                LocationDropdown(
                    selectedLocation = selectedPlace?.name ?: "",
                    locations = placeNames,
                    expanded = placesExpanded,
                    onExpandedChange = { placesExpanded = it },
                    onLocationSelected = { name ->
                        places.firstOrNull { it.name == name }?.let {
                            viewModel.selectPlace(it)
                        }
                        placesExpanded = false
                    }
                )
            }

            Spacer(modifier.height(18.dp))

            selectedLocation?.let { location ->
                weather?.let { weatherData ->
                    val outfitName = outfits.firstOrNull {
                        it.weatherCondition.equals(weatherData.condition, ignoreCase = true)
                    }?.title ?: "Recommended Outfit"

                    val playlistName = playlistMap[location.id] ?: "Today's Soundtrack"

                    LocationRecommendationCard(
                        location = location.name,
                        weatherIcon = painterResource(weatherData.icon),
                        temperature = weatherData.temperature,
                        weather = weatherData.condition,
                        music = playlistName,
                        outfit = outfitName,
                        onViewDetailsClick = {
                            rootNavController.navigate(Routes.locationDetailsRoute(location.id))
                        }
                    )
                }
            }

            selectedPlace?.let { place ->
                Spacer(modifier.height(12.dp))
                PlaceCard(
                    name = place.name,
                    category = place.category,
                    rating = place.rating,
                    onViewPlaceClick = {
                        val recommendationId = place.id.replace("place-", "rec-")
                        rootNavController.navigate(Routes.placeRecommendationDetailRoute(recommendationId))
                    }
                )
            }
        }
    }
}