package com.emirgasic.forecastfm.feature.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.network.location.LocationResponse
import org.maplibre.compose.expressions.dsl.*
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.util.ClickResult

@Composable
fun MarkersLayer(
    jsonString: String,
    locations: List<LocationResponse>,
    places: List<Place>,
    viewModel: FullMapViewModel
) {
    val source = rememberGeoJsonSource(data = GeoJsonData.JsonString(jsonString))

    CircleLayer(
        id = "markers",
        source = source,
        radius = switch(
            input = feature["category"].asString(),
            case("parking", const(5.dp)),
            case("atm", const(5.dp)),
            case("post_box", const(5.dp)),
            fallback = const(7.dp)
        ),
        color = switch(
            input = feature["category"].asString(),
            // Cafes
            case("cafe", const(Color(0xFFC47A44))),
            case("internet_cafe", const(Color(0xFFC47A44))),
            // Restaurants
            case("restaurant", const(Color(0xFFE05A47))),
            case("fast_food", const(Color(0xFFE05A47))),
            // Bars
            case(listOf("bar", "pub", "hookah_lounge"), const(Color(0xFF8E5BB7))),
            // Nightlife
            case(listOf("nightclub", "nightlife", "casino"), const(Color(0xFF5B4B9A))),
            // Accommodation
            case(listOf("hotel", "hostel", "motel", "guest_house", "apartment", "alpine_hut"), const(Color(0xFFB86B77))),
            // Parks
            case(listOf("park", "garden", "picnic_site", "camp_site"), const(Color(0xFF5E9C62))),
            // Viewpoints
            case(listOf("viewpoint", "attraction", "artwork"), const(Color(0xFF607D8B))),
            // Shopping
            case(listOf("supermarket", "shop", "marketplace", "bakery", "clothes", "electronics"), const(Color(0xFF4F86C6))),
            // Finance
            case(listOf("bank", "atm", "bureau_de_change"), const(Color(0xFF388E3C))),
            // Post
            case(listOf("post_office", "post_box", "parcel_locker"), const(Color(0xFFFBC02D))),
            // Health
            case(listOf("hospital", "clinic", "pharmacy", "dentist", "doctors", "veterinary"), const(Color(0xFFD32F2F))),
            // Emergency
            case(listOf("police", "fire_station"), const(Color(0xFF1976D2))),
            // Education
            case(listOf("school", "university", "kindergarten", "college", "library"), const(Color(0xFF009688))),
            // Culture
            case(listOf("museum", "gallery", "theatre", "cinema", "arts_centre"), const(Color(0xFFD39B3D))),
            // Transit
            case(listOf("bus_stop", "bus_station", "taxi", "fuel", "charging_station", "parking"), const(Color(0xFF7B61FF))),
            // Worship
            case(listOf("place_of_worship", "monastery"), const(Color(0xFF81D4FA))),
            // Neighborhoods
            case(listOf("suburb", "neighbourhood", "quarter"), const(Color(0xFFFF9800))),
            // Unknown fallback
            fallback = const(Color(0xFF9E9E9E))
        ),
        strokeWidth = const(2.dp),
        strokeColor = const(Color.White),
        strokeOpacity = const(1f),
        onClick = { features ->
            val props = features.firstOrNull()?.properties ?: return@CircleLayer ClickResult.Pass
            val markerId = props["id"]?.toString()?.removeSurrounding("\"")
            val markerType = props["type"]?.toString()?.removeSurrounding("\"")
            if (markerId != null) {
                when (markerType) {
                    "venue" -> locations.firstOrNull { it.id == markerId }?.let { viewModel.selectLocation(it) }
                    "place" -> places.firstOrNull { it.id == markerId }?.let { viewModel.selectPlace(it) }
                }
            }
            ClickResult.Pass
        }
    )
}