package com.emirgasic.forecastfm.core.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

enum class AppTheme {
    AUTO,
    MORNING,
    AFTERNOON,
    NIGHT
}

object ThemeManager {

    private val _selectedTheme = MutableStateFlow(AppTheme.AUTO)
    val selectedTheme: StateFlow<AppTheme> = _selectedTheme.asStateFlow()

    fun setTheme(theme: AppTheme) {
        _selectedTheme.value = theme
    }

    fun resolveTheme(): AppTheme {
        val selected = _selectedTheme.value
        return if (selected == AppTheme.AUTO) autoThemeForHour() else selected
    }

    fun currentHour(): Int {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    }

    private fun autoThemeForHour(): AppTheme {
        val hour = currentHour()
        return when (hour) {
            in 6..11 -> AppTheme.MORNING
            in 12..17 -> AppTheme.AFTERNOON
            else -> AppTheme.NIGHT
        }
    }
}