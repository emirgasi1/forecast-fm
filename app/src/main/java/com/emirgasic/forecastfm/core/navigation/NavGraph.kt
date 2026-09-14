package com.emirgasic.forecastfm.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.feature.admin.AdminScreen
import com.emirgasic.forecastfm.feature.auth.forgotpassword.ForgotPasswordScreen
import com.emirgasic.forecastfm.feature.auth.login.LoginScreen
import com.emirgasic.forecastfm.feature.auth.register.RegisterScreen
import com.emirgasic.forecastfm.feature.comments.CommentsScreen
import com.emirgasic.forecastfm.feature.feed.FeedScreen
import com.emirgasic.forecastfm.feature.home.HomeScreen
import com.emirgasic.forecastfm.feature.locationdetails.LocationDetailsScreen
import com.emirgasic.forecastfm.feature.locationdetails.placerecommendation.PlaceRecommendationDetailScreen
import com.emirgasic.forecastfm.feature.locationdetails.placerecommendation.PlaceRecommendationScreen
import com.emirgasic.forecastfm.feature.map.MapScreen
import com.emirgasic.forecastfm.feature.map.route.RouteScreen
import com.emirgasic.forecastfm.feature.music.MusicScreen
import com.emirgasic.forecastfm.feature.music.musichistory.MusicHistoryScreen
import com.emirgasic.forecastfm.feature.music.playlist.PlaylistScreen
import com.emirgasic.forecastfm.feature.profile.ProfileScreen
import com.emirgasic.forecastfm.feature.saved.SavedHubScreen
import com.emirgasic.forecastfm.feature.saved.SavedPlaylistsScreen
import com.emirgasic.forecastfm.feature.saved.SavedPostsScreen
import com.emirgasic.forecastfm.feature.saved.SavedStylesScreen
import com.emirgasic.forecastfm.feature.settings.SettingsScreen
import com.emirgasic.forecastfm.feature.settings.edit_profile.EditProfileScreen
import com.emirgasic.forecastfm.feature.splash.SplashScreen
import com.emirgasic.forecastfm.feature.style.StyleScreen
import com.emirgasic.forecastfm.feature.style.detail.StyleDetailScreen
import com.emirgasic.forecastfm.feature.style.posts.NewPostScreen
import com.emirgasic.forecastfm.feature.weather.WeatherScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Login,
    tokenManager: TokenManager
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth Screens
        composable(Routes.Splash) {
            SplashScreen(navController)
        }

        composable(Routes.Login) {
            LoginScreen(
                navController = navController,
                tokenManager = tokenManager
            )
        }

        composable(Routes.Register) {
            RegisterScreen(navController=navController,tokenManager=tokenManager)
        }

        composable(Routes.ForgotPassword) {
            ForgotPasswordScreen(navController=navController)
        }

        // Main Screen with Bottom Navigation
        composable(Routes.Main) {
            MainScreen(
                rootNavController = navController,
                tokenManager = tokenManager
            )
        }

        // Bottom Navigation Screens
        composable(Routes.Home) {
            HomeScreen(
                mainNavController = navController,
                rootNavController = navController,
                tokenManager = tokenManager
            )
        }

        composable(Routes.Music) {
            MusicScreen(
                mainNavController = navController,
                rootNavController = navController,
                tokenManager = tokenManager
            )
        }

        composable(Routes.Map) {
            MapScreen(
                mainNavController = navController,
                rootNavController = navController
            )
        }

        composable(Routes.Style) {
            StyleScreen(
                navController = navController,
                tokenManager = tokenManager
            )
        }

        composable(Routes.Profile) {
            ProfileScreen(
                rootNavController = navController,
                tokenManager = tokenManager
            )
        }

        composable(Routes.Feed) {
            FeedScreen(
                mainNavController = navController,
                rootNavController = navController,
                tokenManager = tokenManager
            )
        }

        // Settings & Edit Profile
        composable(Routes.Settings) {
            SettingsScreen(
                navController = navController,tokenManager=tokenManager
            )
        }
        composable(Routes.SavedHub) {
            SavedHubScreen(
                navController = navController
            )
        }
        composable(Routes.SavedPosts) {
            SavedPostsScreen(
                navController = navController,
                tokenManager = tokenManager
            )
        }
        composable(Routes.SavedPlaylists) {
            SavedPlaylistsScreen(
                navController = navController,
                tokenManager = tokenManager
            )
        }
        composable(Routes.SavedStyles) {
            SavedStylesScreen(
                navController = navController,
                tokenManager = tokenManager
            )
        }

        composable(
            route = Routes.PlaceRecommendationDetail
        ) { backStackEntry ->
            val recommendationId = backStackEntry.arguments?.getString("id")
            PlaceRecommendationDetailScreen(
                navController = navController,
                recommendationId = recommendationId
            )
        }
        composable(Routes.EditProfile) {
            EditProfileScreen(
                navController = navController
            )
        }

        // Playlist
        composable(
            route = Routes.Playlist
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString("playlistId")
            PlaylistScreen(
                navController = navController,
                playlistId = playlistId
            )
        }

        // Location Details
        composable(
            route = Routes.LocationDetails
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getString("location")
            LocationDetailsScreen(
                navController = navController,
                locationId = locationId
            )
        }

        composable(Routes.PlaceRecommendation) {
            PlaceRecommendationScreen(
                navController = navController
            )
        }


        // Music History
        composable(Routes.MusicHistory) {
            MusicHistoryScreen(
                navController = navController,
                tokenManager=tokenManager
            )
        }

        // New Post
        composable(Routes.NewPost) {
            NewPostScreen(
                navController = navController,
                tokenManager=tokenManager
            )
        }

        // Weather
        composable(Routes.Weather) {
            WeatherScreen(
                navController = navController
            )
        }

        // Comments
        composable(
            route = Routes.Comments,
            arguments = listOf(
                navArgument("postId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
                ?: return@composable

            CommentsScreen(
                navController = navController,
                postId = postId,
                tokenManager = tokenManager
            )
        }

        // Admin
        composable(Routes.Admin) {
            AdminScreen(
                navController = navController,
                tokenManager = tokenManager
            )
        }

        composable(
            route = Routes.Route,
            arguments = listOf(
                navArgument("destLat") { type = NavType.StringType },
                navArgument("destLng") { type = NavType.StringType },
                navArgument("originLat") { type = NavType.StringType },
                navArgument("originLng") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val destLat = backStackEntry.arguments?.getString("destLat")?.toDoubleOrNull() ?: 0.0
            val destLng = backStackEntry.arguments?.getString("destLng")?.toDoubleOrNull() ?: 0.0
            val originLat = backStackEntry.arguments?.getString("originLat")?.toDoubleOrNull() ?: 0.0
            val originLng = backStackEntry.arguments?.getString("originLng")?.toDoubleOrNull() ?: 0.0

            RouteScreen(
                navController = navController,
                destinationLat = destLat,
                destinationLng = destLng,
                originLat = originLat,
                originLng = originLng
            )
        }
    }
}