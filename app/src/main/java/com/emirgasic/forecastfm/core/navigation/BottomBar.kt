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
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant){
        NavigationBarItem(
            selected = true,
            onClick = {
                navController.navigate("home")
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
            selected = false,
            onClick = {
                navController.navigate("music")
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
            selected = false,
            onClick = {
                navController.navigate("map")
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
            selected = false,
            onClick = {
                navController.navigate("style")
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
            selected = false,
            onClick = {
                navController.navigate("profile")
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