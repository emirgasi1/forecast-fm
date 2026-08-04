package com.emirgasic.forecastfm.feature.locationdetails.placerecommendation

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.data.model.PlaceRecommendation
import com.emirgasic.forecastfm.data.repository.PlaceRecommendationRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class PlaceRecommendationViewModel : ViewModel() {


    private val repository = PlaceRecommendationRepository()


    private val allRecommendations =
        repository.getRecommendations()


    private val _recommendations =
        MutableStateFlow(allRecommendations)

    val recommendations: StateFlow<List<PlaceRecommendation>> =
        _recommendations.asStateFlow()



    private val _selectedCategory =
        MutableStateFlow("")

    val selectedCategory: StateFlow<String> =
        _selectedCategory.asStateFlow()



    private val _selectedCompanion =
        MutableStateFlow("")

    val selectedCompanion: StateFlow<String> =
        _selectedCompanion.asStateFlow()



    fun selectCategory(category: String){

        _selectedCategory.value = category

        filterRecommendations()

    }



    fun selectCompanion(companion: String){

        _selectedCompanion.value = companion

        filterRecommendations()

    }



    private fun filterRecommendations(){

        val filtered =
            allRecommendations.filter { place ->


                val categoryMatches =
                    _selectedCategory.value.isEmpty() ||
                            place.category == _selectedCategory.value



                val companionMatches =
                    _selectedCompanion.value.isEmpty() ||
                            place.suitableFor.contains(
                                _selectedCompanion.value
                            )


                categoryMatches && companionMatches

            }


        _recommendations.value = filtered

    }

}