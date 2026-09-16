package com.emirgasic.forecastfm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.NavGraph
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.theme.AppTheme
import com.emirgasic.forecastfm.core.theme.ThemeManager
import com.emirgasic.forecastfm.feature.splash.SplashScreen
import com.emirgasic.forecastfm.ui.theme.ForecastfmTheme
import com.emirgasic.forecastfm.ui.theme.colorSchemeFor

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

            val colorScheme = androidx.compose.runtime.remember(selectedTheme) {
                colorSchemeFor(if (selectedTheme == AppTheme.AUTO) ThemeManager.resolveTheme() else selectedTheme)
            }

            ForecastfmTheme(colorScheme = colorScheme) {
                ForecastFMApp(tokenManager = tokenManager)
            }
        }
    }
}

@Composable
fun ForecastFMApp(tokenManager: TokenManager) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        tokenManager.isLoggedIn().collect { loggedIn ->
            isLoggedIn = loggedIn
            isLoading = false
        }
    }

    if (isLoading) {
        SplashScreen()
    } else {
        NavGraph(
            startDestination = if (isLoggedIn) Routes.Main else Routes.Login,
            tokenManager = tokenManager
        )
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Loading...")
    }
}