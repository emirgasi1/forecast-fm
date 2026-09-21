package com.emirgasic.forecastfm.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingState(
    val currentStep: Int = 1,
    val ageGroup: String? = null,
    val companions: Set<String> = emptySet(),
    val weatherPrefs: Set<String> = emptySet(),
    val musicGenres: Set<String> = emptySet(),
    val placeCategories: Set<String> = emptySet(),
    val moods: Set<String> = emptySet()
)

class OnboardingViewModel(
    private val prefs: OnboardingPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    val totalSteps = 6

    fun setAgeGroup(value: String) {
        _state.value = _state.value.copy(ageGroup = value)
    }

    fun toggleCompanion(value: String) {
        val current = _state.value.companions
        val updated = if (value in current) current - value else current + value
        _state.value = _state.value.copy(companions = updated)
    }

    fun toggleWeather(value: String) {
        val current = _state.value.weatherPrefs
        val updated = if (value in current) current - value else current + value
        _state.value = _state.value.copy(weatherPrefs = updated)
    }

    fun toggleMusicGenre(value: String) {
        val current = _state.value.musicGenres
        val updated = if (value in current) current - value else current + value
        _state.value = _state.value.copy(musicGenres = updated)
    }

    fun togglePlaceCategory(value: String) {
        val current = _state.value.placeCategories
        val updated = if (value in current) current - value else current + value
        _state.value = _state.value.copy(placeCategories = updated)
    }

    fun toggleMood(value: String) {
        val current = _state.value.moods
        val updated = if (value in current) current - value else current + value
        _state.value = _state.value.copy(moods = updated)
    }

    fun next() {
        if (_state.value.currentStep < totalSteps) {
            _state.value = _state.value.copy(currentStep = _state.value.currentStep + 1)
        }
    }

    fun back() {
        if (_state.value.currentStep > 1) {
            _state.value = _state.value.copy(currentStep = _state.value.currentStep - 1)
        }
    }

    fun isCurrentStepValid(): Boolean {
        val s = _state.value
        return when (s.currentStep) {
            1 -> s.ageGroup != null
            2 -> s.companions.isNotEmpty()
            3 -> s.weatherPrefs.isNotEmpty()
            4 -> s.musicGenres.isNotEmpty()
            5 -> s.placeCategories.isNotEmpty()
            6 -> s.moods.isNotEmpty()
            else -> true
        }
    }

    fun finish(onDone: () -> Unit) {
        viewModelScope.launch {
            val s = _state.value
            s.ageGroup?.let { prefs.setAgeGroup(it) }
            prefs.setCompanions(s.companions)
            prefs.setWeatherPrefs(s.weatherPrefs)
            prefs.setMusicGenres(s.musicGenres)
            prefs.setPlaceCategories(s.placeCategories)
            prefs.setMoods(s.moods)
            prefs.markCompleted()
            onDone()
        }
    }

    fun skip(onDone: () -> Unit) {
        viewModelScope.launch {
            prefs.markCompleted()
            onDone()
        }
    }
}