package com.emirgasic.forecastfm.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.feature.feed.FeedScreen
import com.emirgasic.forecastfm.feature.home.HomeScreen
import com.emirgasic.forecastfm.feature.map.FullMapScreen
import com.emirgasic.forecastfm.feature.music.MusicScreen
import com.emirgasic.forecastfm.feature.map.MapScreen
import com.emirgasic.forecastfm.feature.style.StyleScreen
import com.emirgasic.forecastfm.feature.style.detail.StyleDetailScreen
import com.emirgasic.forecastfm.feature.profile.ProfileScreen

@Composable
fun MainScreen(
    rootNavController: NavHostController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier
){
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(
                navController = mainNavController
            )
        }
    ){ paddingValues ->

        NavHost(
            navController = mainNavController,
            startDestination = Routes.Home,
            modifier = Modifier.padding(paddingValues)
        ){

            composable(Routes.Home){
                HomeScreen(
                    mainNavController = mainNavController,
                    rootNavController = rootNavController,
                    tokenManager = tokenManager
                )
            }

            composable(Routes.Music){
                MusicScreen(
                    mainNavController = mainNavController,
                    rootNavController = rootNavController,
                    tokenManager=tokenManager
                )
            }

            composable(Routes.Map){
                MapScreen(
                    mainNavController = mainNavController,
                    rootNavController = rootNavController
                )
            }

            composable(Routes.Style) {
                StyleScreen(
                    navController = mainNavController,
                    tokenManager = tokenManager
                )
            }

            composable(
                route = Routes.StyleDetail,
                arguments = listOf(
                    navArgument("outfitId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val outfitId = backStackEntry.arguments?.getString("outfitId")
                StyleDetailScreen(
                    navController = mainNavController,
                    outfitId = outfitId
                )
            }

            composable(Routes.Profile){
                ProfileScreen(
                    rootNavController = rootNavController,
                    tokenManager = tokenManager
                )
            }

            composable(Routes.Feed) {
                FeedScreen(
                    mainNavController = mainNavController,
                    rootNavController = rootNavController,
                    tokenManager = tokenManager
                )
            }
            composable(Routes.FullMap) {
                FullMapScreen(
                    navController = rootNavController
                )
            }
        }
    }
}