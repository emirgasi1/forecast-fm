package com.emirgasic.forecastfm.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import com.emirgasic.forecastfm.core.ui.components.onboarding.OnboardingProgressBar

@Composable
fun OnboardingScreen(
    navController: NavController,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                val context = navController.context
                return OnboardingViewModel(OnboardingPreferences(context)) as T
            }
        }
    )
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .padding(
                top = 60.dp,
                start = 20.dp,
                end = 20.dp,
                bottom = 20.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.currentStep > 1) {
                IconButton(
                    onClick = { viewModel.back() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                Spacer(Modifier.size(32.dp))
            }

            Spacer(Modifier.weight(1f))

            TextButton(
                onClick = {
                    viewModel.skip(onDone = onFinish)
                }
            ) {
                Text(
                    text = "Skip",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        OnboardingProgressBar(
            currentStep = state.currentStep,
            totalSteps = viewModel.totalSteps
        )

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            when (state.currentStep) {
                1 -> OnboardingAgeStep(
                    selected = state.ageGroup,
                    onSelect = { viewModel.setAgeGroup(it) }
                )
                2 -> OnboardingCompanionsStep(
                    selected = state.companions,
                    onToggle = { viewModel.toggleCompanion(it) }
                )
                3 -> OnboardingWeatherStep(
                    selected = state.weatherPrefs,
                    onToggle = { viewModel.toggleWeather(it) }
                )
                4 -> OnboardingMusicStep(
                    selected = state.musicGenres,
                    onToggle = { viewModel.toggleMusicGenre(it) }
                )
                5 -> OnboardingPlacesStep(
                    selected = state.placeCategories,
                    onToggle = { viewModel.togglePlaceCategory(it) }
                )
                6 -> OnboardingMoodStep(
                    selected = state.moods,
                    onToggle = { viewModel.toggleMood(it) }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (state.currentStep == viewModel.totalSteps) {
                    viewModel.finish(onDone = onFinish)
                } else {
                    viewModel.next()
                }
            },
            enabled = viewModel.isCurrentStepValid(),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Text(
                text = if (state.currentStep == viewModel.totalSteps) "Finish" else "Continue",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}