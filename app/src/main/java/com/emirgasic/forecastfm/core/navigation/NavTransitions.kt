package com.emirgasic.forecastfm.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry

private const val DURATION = 260


fun drillDownEnter(): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { it / 3 },
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
    ) + fadeIn(tween(DURATION))

fun drillDownExit(): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { -it / 4 },
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
    ) + fadeOut(tween(DURATION))

fun drillDownPopEnter(): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { -it / 4 },
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
    ) + fadeIn(tween(DURATION))

fun drillDownPopExit(): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { it / 3 },
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
    ) + fadeOut(tween(DURATION))

fun modalEnter(): EnterTransition =
    slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
    )

fun modalExit(): ExitTransition =
    ExitTransition.None

fun modalPopEnter(): EnterTransition =
    EnterTransition.None

fun modalPopExit(): ExitTransition =
    slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
    )

fun tabEnter(): EnterTransition = EnterTransition.None
fun tabExit(): ExitTransition = ExitTransition.None