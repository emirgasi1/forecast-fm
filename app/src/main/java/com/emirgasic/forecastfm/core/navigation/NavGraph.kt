package com.emirgasic.forecastfm.core.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emirgasic.forecastfm.feature.auth.forgotpassword.ForgotPasswordScreen
import com.emirgasic.forecastfm.feature.auth.login.LoginScreen
import com.emirgasic.forecastfm.feature.auth.register.RegisterScreen
import com.emirgasic.forecastfm.feature.home.HomeScreen
import com.emirgasic.forecastfm.feature.splash.SplashScreen

@Composable
fun NavGraph(modifier:Modifier=Modifier){


    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home
    ) {

        composable(Routes.Splash) {
            SplashScreen(navController=navController)
        }
        composable(Routes.Login){
            LoginScreen(navController=navController)
        }
        composable(Routes.Register){
            RegisterScreen(navController=navController)
        }
        composable(Routes.ForgotPassword){
            ForgotPasswordScreen(navController=navController)
        }
        composable(Routes.Home){
            HomeScreen(navController=navController)
        }


    }

}