package com.emirgasic.forecastfm.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emirgasic.forecastfm.feature.home.HomeScreen
import com.emirgasic.forecastfm.feature.music.MusicScreen
import com.emirgasic.forecastfm.feature.map.MapScreen
import com.emirgasic.forecastfm.feature.style.StyleScreen
import com.emirgasic.forecastfm.feature.profile.ProfileScreen


@Composable
fun MainScreen(){

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
                    navController = mainNavController
                )

            }


            composable(Routes.Music){

                MusicScreen(
                    navController = mainNavController
                )

            }


            composable(Routes.Map){

                MapScreen(
                    navController = mainNavController
                )

            }


            composable(Routes.Style){

                StyleScreen(
                    navController = mainNavController
                )

            }


            composable(Routes.Profile){

                ProfileScreen(
                    navController = mainNavController
                )

            }

        }

    }

}