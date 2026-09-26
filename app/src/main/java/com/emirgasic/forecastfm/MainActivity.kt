package com.emirgasic.forecastfm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.NavGraph
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import com.emirgasic.forecastfm.core.theme.AppTheme
import com.emirgasic.forecastfm.core.theme.ThemeManager
import com.emirgasic.forecastfm.feature.splash.SplashScreen
import com.emirgasic.forecastfm.ui.theme.ForecastfmTheme
import com.emirgasic.forecastfm.ui.theme.colorSchemeFor
import com.emirgasic.forecastfm.ui.theme.forecastColorsFor
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {

    private lateinit var tokenManager: TokenManager

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        tokenManager = TokenManager(this)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val selectedTheme by ThemeManager.selectedTheme.collectAsState()

            val resolvedTheme = remember(selectedTheme) {
                if (selectedTheme == AppTheme.AUTO) ThemeManager.resolveTheme() else selectedTheme
            }

            val colorScheme = remember(resolvedTheme) {
                colorSchemeFor(resolvedTheme)
            }

            val forecastColors = remember(resolvedTheme) {
                forecastColorsFor(resolvedTheme)
            }

            ForecastfmTheme(
                colorScheme = colorScheme,
                forecastColors = forecastColors
            ) {
                ForecastFMApp(tokenManager = tokenManager)
            }
        }
    }
}

@Composable
fun ForecastFMApp(tokenManager: TokenManager) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var hasCompletedOnboarding by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val onboardingPrefs = remember { OnboardingPreferences(context) }

    LaunchedEffect(Unit) {
        val minimumSplash = async { delay(2000) }

        isLoggedIn = tokenManager.isLoggedIn().first()
        hasCompletedOnboarding = onboardingPrefs.isCompleted()

        minimumSplash.await()
        isLoading = false
    }

    if (isLoading) {
        SplashScreen()
    } else {
        val start = when {
            !isLoggedIn -> Routes.Login
            !hasCompletedOnboarding -> Routes.Onboarding
            else -> Routes.Main
        }

        NavGraph(
            startDestination = start,
            tokenManager = tokenManager
        )
    }
}