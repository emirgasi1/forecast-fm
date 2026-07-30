package com.emirgasic.forecastfm.core.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R

@Composable
fun BottomBar(navController: NavController){
    val currentRoute =
        navController.currentBackStackEntry?.destination?.route

    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant){
        NavigationBarItem(
            selected = currentRoute == Routes.Home,
            onClick = {
                navController.navigate(Routes.Home)
            },
            icon = {

                Image(
                    painter = painterResource(R.drawable.house),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )

            },
            label = {
                Text(
                    text = "Home"
                )
            }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Music,
            onClick = {
                navController.navigate(Routes.Music)
            },
            icon = {

                Image(
                    painter = painterResource(R.drawable.music),
                    contentDescription = "Music",
                    modifier = Modifier.size(24.dp)
                )

            },
            label = {
                Text(
                    text = "Music"
                )
            }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Map,
            onClick = {
                navController.navigate(Routes.Map)
            },
            icon = {

                Image(
                    painter = painterResource(R.drawable.placeholder),
                    contentDescription = "Map",
                    modifier = Modifier.size(24.dp)
                )

            },
            label = {
                Text(
                    text = "Map"
                )
            }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Style,
            onClick = {
                navController.navigate(Routes.Style)
            },
            icon = {

                Image(
                    painter = painterResource(R.drawable.clothes),
                    contentDescription = "Style",
                    modifier = Modifier.size(24.dp)
                )

            },
            label = {
                Text(
                    text = "Style"
                )
            }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Feed,
            onClick = {
                navController.navigate(Routes.Feed)
            },
            icon = {

                Image(
                    painter = painterResource(R.drawable.heart),
                    contentDescription = "Feed",
                    modifier = Modifier.size(24.dp)
                )

            },
            label = {
                Text(
                    text = "Feed"
                )
            }
        )
        NavigationBarItem(
            selected = currentRoute == Routes.Profile,
            onClick = {
                navController.navigate(Routes.Profile)
            },
            icon = {

                Image(
                    painter = painterResource(R.drawable.cogwheel),
                    contentDescription = "profile",
                    modifier = Modifier.size(24.dp)
                )

            },
            label = {
                Text(
                    text = "Profile"
                )
            }
        )
    }
}