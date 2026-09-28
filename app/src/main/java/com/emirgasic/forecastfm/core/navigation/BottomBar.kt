package com.emirgasic.forecastfm.core.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.shapes.seasonShape
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

private data class BottomBarItem(
    val route: String,
    val label: String,
    val iconOutlined: Int,
    val iconFilled: Int
)

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

    val barHeight = 76.dp
    val indicatorHeight = 60.dp
    val indicatorHorizontalPadding = 6.dp

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val itemWidth = screenWidth / items.size
    val indicatorWidth = itemWidth - (indicatorHorizontalPadding * 2)
    val targetOffsetX = itemWidth * selectedIndex + indicatorHorizontalPadding

    val indicatorOffsetX by animateDpAsState(
        targetValue = targetOffsetX,
        animationSpec = tween(durationMillis = 320),
        label = "bottomBarIndicatorX"
    )

    val indicatorShape = remember { seasonShape() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(forecastColors.card)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = (barHeight - indicatorHeight) / 2)
                    .offset(x = indicatorOffsetX)
                    .width(indicatorWidth)
                    .height(indicatorHeight)
                    .clip(indicatorShape)
                    .background(forecastColors.background, indicatorShape)
                    .border(1.dp, forecastColors.border, indicatorShape)
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

@Composable
private fun BottomBarItemContent(
    item: BottomBarItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val forecastColors = LocalForecastColors.current

    val iconColor by animateColorAsState(
        targetValue = if (selected) forecastColors.title else forecastColors.muted,
        animationSpec = tween(durationMillis = 250),
        label = "bottomBarIconColor"
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) forecastColors.title else forecastColors.muted,
        animationSpec = tween(durationMillis = 250),
        label = "bottomBarLabelColor"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(if (selected) item.iconFilled else item.iconOutlined),
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = item.label,
            color = labelColor,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}