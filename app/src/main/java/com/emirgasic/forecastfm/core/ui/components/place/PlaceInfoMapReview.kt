package com.emirgasic.forecastfm.core.ui.components.place

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.theme.AppTheme
import com.emirgasic.forecastfm.core.theme.ThemeManager
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position

@Composable
fun PlaceInfoMapPreview(
    latitude: Double,
    longitude: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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

    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(latitude = latitude, longitude = longitude),
            zoom = 15.0
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            baseStyle = BaseStyle.Json(styleJson)
        ) {
            val markerJson = """
                {
                    "type": "FeatureCollection",
                    "features": [
                        {
                            "type": "Feature",
                            "geometry": {
                                "type": "Point",
                                "coordinates": [$longitude, $latitude]
                            },
                            "properties": {}
                        }
                    ]
                }
            """.trimIndent()

            val source = rememberGeoJsonSource(
                data = GeoJsonData.JsonString(markerJson)
            )

            CircleLayer(
                id = "place-preview-marker",
                source = source,
                radius = const(12.dp),
                color = const(Color(0xFFD97706)),
                strokeWidth = const(3.dp),
                strokeColor = const(Color.White),
                strokeOpacity = const(1f)
            )
        }
    }
}