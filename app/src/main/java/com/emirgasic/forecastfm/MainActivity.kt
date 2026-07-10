package com.emirgasic.forecastfm

import android.os.Bundle
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
import com.emirgasic.forecastfm.feature.playground.ComposePlayground
import com.emirgasic.forecastfm.ui.theme.ForecastfmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForecastfmTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ComposePlayground(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


