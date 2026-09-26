package com.emirgasic.forecastfm.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.security.AdminConfig
import com.emirgasic.forecastfm.core.ui.components.settings.SettingsOptionCard
import com.emirgasic.forecastfm.core.ui.components.settings.SettingsSection
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun SettingsScreen(
    navController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(tokenManager) as T
            }
        }
    )
) {

    val accountOptions by viewModel.accountOptions.collectAsState()
    val preferenceOptions by viewModel.preferenceOptions.collectAsState()
    val aboutOptions by viewModel.aboutOptions.collectAsState()
    var showAdminPin by remember { mutableStateOf(false) }

    val forecastColors = LocalForecastColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 60.dp,
                start = 10.dp,
                end = 10.dp
            )
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.Start,
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            item {
                Text(
                    text = "Settings",
                    color = forecastColors.title,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                SettingsSection(title = "Account") {
                    accountOptions.forEach { option ->
                        SettingsOptionCard(
                            title = option.title,
                            onClick = {
                                option.route?.let {
                                    navController.navigate(it)
                                }
                            }
                        )
                    }
                }
            }

            item {
                SettingsSection(title = "Preferences") {
                    preferenceOptions.forEach { option ->
                        SettingsOptionCard(
                            title = option.title,
                            onClick = {
                                option.route?.let { route ->
                                    navController.navigate(route)
                                }
                            }
                        )
                    }

                    SettingsOptionCard(
                        title = "Admin",
                        onClick = { showAdminPin = true }
                    )
                }
            }

            item {
                SettingsSection(title = "About") {
                    aboutOptions.forEach { option ->
                        SettingsOptionCard(
                            title = option.title,
                            onClick = {
                                option.route?.let { route ->
                                    navController.navigate(route)
                                }
                            }
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        viewModel.logout()
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.Main) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = forecastColors.error,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        if (showAdminPin) {
            AdminPinDialog(
                onDismiss = { showAdminPin = false },
                onSubmit = { entered ->
                    if (entered == AdminConfig.PIN) {
                        navController.navigate(Routes.Admin)
                        true
                    } else {
                        false
                    }
                }
            )
        }
    }
}