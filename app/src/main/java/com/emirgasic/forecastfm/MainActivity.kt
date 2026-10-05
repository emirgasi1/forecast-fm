package com.emirgasic.forecastfm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
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
        android.util.Log.d("ColdStart", "onCreate at ${android.os.SystemClock.elapsedRealtime()}")
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
    var showApp by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val onboardingPrefs = remember { OnboardingPreferences(context) }

    LaunchedEffect(Unit) {
        val startMs = android.os.SystemClock.elapsedRealtime()
        android.util.Log.d("ColdStart", "LaunchedEffect start at $startMs")

        val minimumSplash = async { delay(2000) }

        isLoggedIn = tokenManager.isLoggedIn().first()
        hasCompletedOnboarding = onboardingPrefs.isCompleted()

        minimumSplash.await()


        val stage2Ms = android.os.SystemClock.elapsedRealtime()
        android.util.Log.d("ColdStart", "showApp = true at $stage2Ms (took ${stage2Ms - startMs}ms)")
        showApp = true

        withFrameNanos { }

        val endMs = android.os.SystemClock.elapsedRealtime()
        android.util.Log.d("ColdStart", "isLoading = false at $endMs (took ${endMs - startMs}ms)")
        isLoading = false
    }

    val startDestination = when {
        !isLoggedIn -> Routes.Login
        !hasCompletedOnboarding -> Routes.Onboarding
        else -> Routes.Main
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showApp) {
            NavGraph(
                startDestination = startDestination,
                tokenManager = tokenManager
            )
        }

        if (isLoading) {
            SplashScreen()
        }
    }
}