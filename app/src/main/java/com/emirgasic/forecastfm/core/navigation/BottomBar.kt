package com.emirgasic.forecastfm.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun BottomBar(navController: NavController) {
    val currentRoute =
        navController.currentBackStackEntry?.destination?.route

    val forecastColors = LocalForecastColors.current

    val selectedIconColor = MaterialTheme.colorScheme.primary
    val unselectedIconColor = forecastColors.muted
    val selectedLabelColor = MaterialTheme.colorScheme.primary
    val unselectedLabelColor = forecastColors.muted
    val indicatorColor = forecastColors.primary.copy(alpha = 0.15f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .navigationBarsPadding()
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            NavigationBarItem(
                selected = currentRoute == Routes.Home,
                onClick = { navController.navigate(Routes.Home) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedIconColor,
                    unselectedIconColor = unselectedIconColor,
                    selectedTextColor = selectedLabelColor,
                    unselectedTextColor = unselectedLabelColor,
                    indicatorColor = indicatorColor
                ),
                icon = {
                    val selected = currentRoute == Routes.Home
                    Icon(
                        painter = painterResource(
                            if (selected) R.drawable.house_filled else R.drawable.house
                        ),
                        contentDescription = "Home",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = "Home") }
            )
            NavigationBarItem(
                selected = currentRoute == Routes.Music,
                onClick = { navController.navigate(Routes.Music) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedIconColor,
                    unselectedIconColor = unselectedIconColor,
                    selectedTextColor = selectedLabelColor,
                    unselectedTextColor = unselectedLabelColor,
                    indicatorColor = indicatorColor
                ),
                icon = {
                    val selected = currentRoute == Routes.Music
                    Icon(
                        painter = painterResource(
                            if (selected) R.drawable.music_filled else R.drawable.music
                        ),
                        contentDescription = "Music",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = "Music") }
            )
            NavigationBarItem(
                selected = currentRoute == Routes.Map,
                onClick = { navController.navigate(Routes.Map) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedIconColor,
                    unselectedIconColor = unselectedIconColor,
                    selectedTextColor = selectedLabelColor,
                    unselectedTextColor = unselectedLabelColor,
                    indicatorColor = indicatorColor
                ),
                icon = {
                    val selected = currentRoute == Routes.Map
                    Icon(
                        painter = painterResource(
                            if (selected) R.drawable.placeholder_filled else R.drawable.placeholder
                        ),
                        contentDescription = "Map",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = "Map") }
            )
            NavigationBarItem(
                selected = currentRoute == Routes.Style,
                onClick = { navController.navigate(Routes.Style) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedIconColor,
                    unselectedIconColor = unselectedIconColor,
                    selectedTextColor = selectedLabelColor,
                    unselectedTextColor = unselectedLabelColor,
                    indicatorColor = indicatorColor
                ),
                icon = {
                    val selected = currentRoute == Routes.Style
                    Icon(
                        painter = painterResource(
                            if (selected) R.drawable.clothes_filled else R.drawable.clothes
                        ),
                        contentDescription = "Style",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = "Style") }
            )
            NavigationBarItem(
                selected = currentRoute == Routes.Feed,
                onClick = { navController.navigate(Routes.Feed) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedIconColor,
                    unselectedIconColor = unselectedIconColor,
                    selectedTextColor = selectedLabelColor,
                    unselectedTextColor = unselectedLabelColor,
                    indicatorColor = indicatorColor
                ),
                icon = {
                    val selected = currentRoute == Routes.Feed
                    Icon(
                        painter = painterResource(
                            if (selected) R.drawable.heart_filled else R.drawable.heart
                        ),
                        contentDescription = "Feed",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = "Feed") }
            )
            NavigationBarItem(
                selected = currentRoute == Routes.Profile,
                onClick = { navController.navigate(Routes.Profile) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedIconColor,
                    unselectedIconColor = unselectedIconColor,
                    selectedTextColor = selectedLabelColor,
                    unselectedTextColor = unselectedLabelColor,
                    indicatorColor = indicatorColor
                ),
                icon = {
                    val selected = currentRoute == Routes.Profile
                    Icon(
                        painter = painterResource(
                            if (selected) R.drawable.cogwheel_filled else R.drawable.cogwheel
                        ),
                        contentDescription = "Profile",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = "Profile") }
            )
        }
    }
}