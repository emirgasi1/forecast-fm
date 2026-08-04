package com.emirgasic.forecastfm.data.model

data class PlaceRecommendation(

    val id: String,

    val name: String,

    val category: String,

    val location: String,

    val description: String,

    val suitableFor: List<String>,

    val weatherCondition: String,

    val rating: Double

)