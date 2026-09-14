package com.emirgasic.forecastfm.feature.map.route

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.ui.components.route.ModeChip
import com.emirgasic.forecastfm.core.ui.components.route.RouteMap
import com.emirgasic.forecastfm.core.utils.PolylineDecoder
import com.emirgasic.forecastfm.core.utils.RouteFormatter
import com.emirgasic.forecastfm.network.route.RouteApi

@Composable
fun RouteScreen(
    navController: NavController,
    destinationLat: Double,
    destinationLng: Double,
    originLat: Double,
    originLng: Double,
    modifier: Modifier = Modifier,
    viewModel: RouteViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return RouteViewModel(RouteApi()) as T
            }
        }
    )
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val mode by viewModel.mode.collectAsState()

    val styleJson = remember {
        context.resources.openRawResource(R.raw.map_style_morning)
            .bufferedReader()
            .use { it.readText() }
    }

    LaunchedEffect(destinationLat, destinationLng) {
        viewModel.setOrigin(originLat, originLng)
        viewModel.fetchRoute(context, destinationLat, destinationLng)
    }

    val routePoints = remember(state) {
        (state as? RouteState.Success)
            ?.let { PolylineDecoder.decode(it.route.geometry) }
            ?: emptyList()
    }

    val routeColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        RouteMap(
            routePoints = routePoints,
            originLat = originLat,
            originLng = originLng,
            destinationLat = destinationLat,
            destinationLng = destinationLng,
            styleJson = styleJson,
            routeColor = routeColor
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            when (val s = state) {
                RouteState.Idle, RouteState.Loading -> {
                    Text("Loading route...", color = MaterialTheme.colorScheme.onSurface)
                }
                is RouteState.Error -> {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                }
                is RouteState.Success -> {
                    Text(
                        text = RouteFormatter.formatDistance(s.route.distanceMeters),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "About ${RouteFormatter.formatDuration(s.route.durationSeconds)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeChip(
                    text = "Walk",
                    selected = mode == TravelMode.WALKING,
                    onClick = { viewModel.setMode(TravelMode.WALKING, context) }
                )
                ModeChip(
                    text = "Drive",
                    selected = mode == TravelMode.DRIVING,
                    onClick = { viewModel.setMode(TravelMode.DRIVING, context) }
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val navMode = if (mode == TravelMode.WALKING) "w" else "d"
                    val gmmIntentUri = Uri.parse(
                        "google.navigation:q=$destinationLat,$destinationLng&mode=$navMode"
                    )
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")

                    try {
                        context.startActivity(mapIntent)
                    } catch (e: ActivityNotFoundException) {
                        val fallback = Uri.parse(
                            "geo:$destinationLat,$destinationLng?q=$destinationLat,$destinationLng"
                        )
                        context.startActivity(Intent(Intent.ACTION_VIEW, fallback))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text("Navigate")
            }
        }
    }
}