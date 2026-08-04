package com.emirgasic.forecastfm.feature.locationdetails

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.data.model.LocationDetails
import com.emirgasic.forecastfm.data.repository.LocationDetailsRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationDetailsViewModel : ViewModel() {


    private val repository = LocationDetailsRepository()


    private val _locationDetails =
        MutableStateFlow<LocationDetails?>(null)

    val locationDetails: StateFlow<LocationDetails?> =
        _locationDetails.asStateFlow()



    fun loadLocation(location: String) {

        val details =
            repository.getLocationDetails(location)

        _locationDetails.value = details
    }

}