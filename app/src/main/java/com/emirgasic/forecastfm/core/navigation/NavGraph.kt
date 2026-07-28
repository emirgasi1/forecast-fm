package com.emirgasic.forecastfm.core.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emirgasic.forecastfm.feature.auth.forgotpassword.ForgotPasswordScreen
import com.emirgasic.forecastfm.feature.auth.login.LoginScreen
import com.emirgasic.forecastfm.feature.auth.register.RegisterScreen
import com.emirgasic.forecastfm.feature.feed.FeedScreen
import com.emirgasic.forecastfm.feature.home.HomeScreen
import com.emirgasic.forecastfm.feature.map.MapScreen
import com.emirgasic.forecastfm.feature.music.MusicScreen
import com.emirgasic.forecastfm.feature.music.musichistory.MusicHistoryScreen
import com.emirgasic.forecastfm.feature.music.playlist.PlaylistScreen
import com.emirgasic.forecastfm.feature.profile.ProfileScreen
import com.emirgasic.forecastfm.feature.settings.SettingsScreen
import com.emirgasic.forecastfm.feature.settings.edit_profile.EditProfileScreen
import com.emirgasic.forecastfm.feature.splash.SplashScreen
import com.emirgasic.forecastfm.feature.style.StyleScreen
import com.emirgasic.forecastfm.feature.style.posts.NewPostScreen

@Composable
fun NavGraph(modifier: Modifier = Modifier){

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Main
    ){

        composable(Routes.Splash){
            SplashScreen(navController)
        }

        composable(Routes.Login){
            LoginScreen(navController)
        }

        composable(Routes.Register){
            RegisterScreen(navController)
        }

        composable(Routes.ForgotPassword){
            ForgotPasswordScreen(navController)
        }

        composable(Routes.Main){

            MainScreen(
                rootNavController = navController
            )

        }

        composable(Routes.Settings){

            SettingsScreen(
                navController = navController
            )

        }
        composable(Routes.EditProfile){

            EditProfileScreen(
                navController = navController
            )

        }
        composable(Routes.Playlist){

            PlaylistScreen(
                navController = navController
            )

        }
        composable(Routes.MusicHistory){

            MusicHistoryScreen(
                navController = navController
            )

        }
        composable(Routes.NewPost){
            NewPostScreen(
                navController=navController
            )
        }
    }
}