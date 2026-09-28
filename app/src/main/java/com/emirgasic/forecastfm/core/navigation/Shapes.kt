package com.emirgasic.forecastfm.core.navigation

import java.time.LocalDate
import java.time.Month

enum class Season {
    Spring,
    Summer,
    Autumn,
    Winter
}

object SeasonProvider {

    val DEBUG_FORCE_SEASON: Season? = null

    fun currentSeason(date: LocalDate = LocalDate.now()): Season {
        DEBUG_FORCE_SEASON?.let { return it }
        return seasonForMonth(date.month)
    }

    fun seasonForMonth(month: Month): Season = when (month) {
        Month.MARCH, Month.APRIL, Month.MAY -> Season.Spring
        Month.JUNE, Month.JULY, Month.AUGUST -> Season.Summer
        Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> Season.Autumn
        Month.DECEMBER, Month.JANUARY, Month.FEBRUARY -> Season.Winter
    }
}