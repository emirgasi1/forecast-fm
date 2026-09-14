package com.emirgasic.forecastfm.feature.map.route

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.feature.map.LocationManager
import com.emirgasic.forecastfm.network.route.RouteApi
import com.emirgasic.forecastfm.network.route.RouteRequest
import com.emirgasic.forecastfm.network.route.RouteResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RouteState {
    object Idle : RouteState()
    object Loading : RouteState()
    data class Success(val route: RouteResponse) : RouteState()
    data class Error(val message: String) : RouteState()
}

enum class TravelMode {
    WALKING,
    DRIVING
}

class RouteViewModel(
    private val routeApi: RouteApi
) : ViewModel() {

    private val _state = MutableStateFlow<RouteState>(RouteState.Idle)
    val state: StateFlow<RouteState> = _state.asStateFlow()

    private val _mode = MutableStateFlow(TravelMode.WALKING)
    val mode: StateFlow<TravelMode> = _mode.asStateFlow()

    private var currentDestination: Pair<Double, Double>? = null
    private var lastKnownOrigin: Pair<Double, Double>? = null

    fun setOrigin(lat: Double, lng: Double) {
        lastKnownOrigin = lat to lng
    }

    fun setMode(newMode: TravelMode, context: Context) {
        if (_mode.value == newMode) return
        _mode.value = newMode

        val dest = currentDestination ?: return
        fetchRoute(context, dest.first, dest.second)
    }

    fun fetchRoute(context: Context, destLat: Double, destLng: Double) {
        currentDestination = destLat to destLng

        viewModelScope.launch {
            _state.value = RouteState.Loading

            val origin = lastKnownOrigin ?: run {
                val location = LocationManager(context).getCurrentLocation()
                if (location == null) {
                    _state.value = RouteState.Error("Could not get your location")
                    return@launch
                }
                val pair = location.latitude to location.longitude
                lastKnownOrigin = pair
                pair
            }

            val request = RouteRequest(
                fromLat = origin.first,
                fromLng = origin.second,
                toLat = destLat,
                toLng = destLng,
                mode = when (_mode.value) {
                    TravelMode.WALKING -> "walking"
                    TravelMode.DRIVING -> "driving"
                }
            )

            try {
                val response = routeApi.getRoute(request)
                _state.value = RouteState.Success(response)
            } catch (e: Exception) {
                _state.value = RouteState.Error(e.message ?: "Route failed")
            }
        }
    }

    fun clearRoute() {
        _state.value = RouteState.Idle
        currentDestination = null
    }
}