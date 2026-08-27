package com.emirgasic.forecastfm

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.emirgasic.forecastfm.core.navigation.NavGraph
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.feature.playground.ComposePlayground
import com.emirgasic.forecastfm.feature.splash.SplashScreen
import com.emirgasic.forecastfm.network.user.UserApi
import com.emirgasic.forecastfm.ui.theme.ForecastfmTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ForecastfmTheme {
                NavGraph()
            }
        }
    }
}
