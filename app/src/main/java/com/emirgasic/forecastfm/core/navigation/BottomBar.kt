package com.emirgasic.forecastfm.core.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.shapes.seasonShape
import com.emirgasic.forecastfm.core.ui.components.common.BottomBarItemContent
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun BottomBar(navController: NavController) {
    val forecastColors = LocalForecastColors.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val items = remember {
        listOf(
            BottomBarItem(Routes.Home, "Home", R.drawable.house, R.drawable.house_filled),
            BottomBarItem(Routes.Music, "Music", R.drawable.music, R.drawable.music_filled),
            BottomBarItem(Routes.Map, "Map", R.drawable.placeholder, R.drawable.placeholder_filled),
            BottomBarItem(Routes.Style, "Style", R.drawable.clothes, R.drawable.clothes_filled),
            BottomBarItem(Routes.Feed, "Feed", R.drawable.heart, R.drawable.heart_filled),
            BottomBarItem(Routes.Profile, "Profile", R.drawable.cogwheel, R.drawable.cogwheel_filled)
        )
    }

    val selectedIndex = items.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    val barHeight = 60.dp
    val circleSize = 48.dp

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val itemWidth = screenWidth / items.size

    val targetOffsetX = itemWidth * selectedIndex + (itemWidth - circleSize) / 2

    val circleOffsetX by animateDpAsState(
        targetValue = targetOffsetX,
        animationSpec = tween(
            durationMillis = 260,
            easing = FastOutSlowInEasing
        ),
        label = "bottomBarCircleOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(forecastColors.background, seasonShape())
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
        ) {
            Box(
                modifier = Modifier
                    .offset(
                        x = circleOffsetX,
                        y = (barHeight - circleSize) / 2
                    )
                    .size(circleSize)
                    .clip(CircleShape)
                    .background(forecastColors.card)
                    .border(
                        width = 1.dp,
                        color = forecastColors.border,
                        shape = CircleShape
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val selected = index == selectedIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(barHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        BottomBarItemContent(
                            item = item,
                            selected = selected,
                            onClick = {
                                if (item.route != currentRoute) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}