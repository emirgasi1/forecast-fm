package com.emirgasic.forecastfm.feature.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class LocationManager(
    context: Context
) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        Log.d("LocMgr", "getCurrentLocation() started")

        val fresh = suspendCancellableCoroutine { continuation ->
            fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    Log.d("LocMgr", "getCurrentLocation success: lat=${location?.latitude}, lng=${location?.longitude}")
                    continuation.resume(location)
                }
                .addOnFailureListener { e ->
                    Log.e("LocMgr", "getCurrentLocation failed: ${e.message}")
                    continuation.resume(null)
                }
        }

        if (fresh != null && isValid(fresh)) {
            Log.d("LocMgr", "Returning fresh location")
            return fresh
        }
        Log.d("LocMgr", "Fresh invalid or null, trying lastLocation")

        val last = try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            Log.e("LocMgr", "lastLocation exception: ${e.message}")
            null
        }
        Log.d("LocMgr", "lastLocation: lat=${last?.latitude}, lng=${last?.longitude}")

        if (last != null && isValid(last)) {
            Log.d("LocMgr", "Returning last location")
            return last
        }

        Log.d("LocMgr", "No valid location, returning null")
        return null
    }

    private fun isValid(location: Location): Boolean {
        return location.latitude != 0.0 && location.longitude != 0.0
    }
}