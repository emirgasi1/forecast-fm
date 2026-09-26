package com.emirgasic.forecastfm.core.ui.components.route

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.core.utils.RouteGeoJson
import com.emirgasic.forecastfm.core.utils.RouteZoomCalculator
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position

@Composable
fun RouteMap(
    routePoints: List<Pair<Double, Double>>,
    originLat: Double,
    originLng: Double,
    destinationLat: Double,
    destinationLng: Double,
    styleJson: String,
    routeColor: Color,
    modifier: Modifier = Modifier
) {
    val forecastColors = LocalForecastColors.current

    val originMarkerColor = forecastColors.primary
    val destinationMarkerColor = forecastColors.accent

    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(
                latitude = (originLat + destinationLat) / 2,
                longitude = (originLng + destinationLng) / 2
            ),
            zoom = 12.5
        )
    )

    LaunchedEffect(routePoints) {
        if (routePoints.isNotEmpty()) {
            val lats = routePoints.map { it.first } + listOf(originLat, destinationLat)
            val lngs = routePoints.map { it.second } + listOf(originLng, destinationLng)

            val minLat = lats.minOrNull() ?: 43.85
            val maxLat = lats.maxOrNull() ?: 43.86
            val minLng = lngs.minOrNull() ?: 18.41
            val maxLng = lngs.maxOrNull() ?: 18.43

            val centerLat = (minLat + maxLat) / 2
            val centerLng = (minLng + maxLng) / 2
            val latSpan = maxLat - minLat

            cameraState.animateTo(
                CameraPosition(
                    target = Position(
                        latitude = centerLat + latSpan * 0.25,
                        longitude = centerLng
                    ),
                    zoom = RouteZoomCalculator.calculateZoom(minLat, maxLat, minLng, maxLng) - 0.8
                )
            )
        }
    }

    MaplibreMap(
        modifier = modifier.fillMaxSize(),
        cameraState = cameraState,
        baseStyle = BaseStyle.Json(styleJson)
    ) {
        if (routePoints.isNotEmpty()) {
            val routeJson = RouteGeoJson.buildRouteJson(routePoints)
            val routeSource = rememberGeoJsonSource(
                data = GeoJsonData.JsonString(routeJson)
            )

            LineLayer(
                id = "route-line",
                source = routeSource,
                color = const(routeColor),
                width = const(6.dp)
            )
        }

        val originJson = RouteGeoJson.buildPointJson(originLat, originLng)
        val originSource = rememberGeoJsonSource(
            data = GeoJsonData.JsonString(originJson)
        )

        CircleLayer(
            id = "route-origin",
            source = originSource,
            radius = const(8.dp),
            color = const(originMarkerColor),
            strokeWidth = const(3.dp),
            strokeColor = const(Color.White),
            strokeOpacity = const(1f)
        )

        val destJson = RouteGeoJson.buildPointJson(destinationLat, destinationLng)
        val destSource = rememberGeoJsonSource(
            data = GeoJsonData.JsonString(destJson)
        )

        CircleLayer(
            id = "route-destination",
            source = destSource,
            radius = const(10.dp),
            color = const(destinationMarkerColor),
            strokeWidth = const(3.dp),
            strokeColor = const(Color.White),
            strokeOpacity = const(1f)
        )
    }
}