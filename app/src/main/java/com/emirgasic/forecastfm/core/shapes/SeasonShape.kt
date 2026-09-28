package com.emirgasic.forecastfm.core.shapes

import androidx.compose.ui.graphics.Shape
import com.emirgasic.forecastfm.core.navigation.Season
import com.emirgasic.forecastfm.core.navigation.SeasonProvider

fun seasonShape(season: Season = SeasonProvider.currentSeason()): Shape = when (season) {
    Season.Spring -> SpringShape()
    Season.Summer -> SummerShape()
    Season.Autumn -> AutumnShape()
    Season.Winter -> WinterShape()
}