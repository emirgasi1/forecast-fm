package com.emirgasic.forecastfm.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.theme.AppTheme
import com.emirgasic.forecastfm.core.ui.components.admin.AdminApiUrlEditor
import com.emirgasic.forecastfm.core.ui.components.admin.AdminDebugRow
import com.emirgasic.forecastfm.core.ui.components.admin.AdminSectionTitle
import com.emirgasic.forecastfm.core.ui.components.admin.AdminThemeOption

@Composable
fun AdminScreen(
    navController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: AdminViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AdminViewModel(tokenManager) as T
            }
        }
    )
) {

    val currentTheme by viewModel.currentTheme.collectAsState()
    val currentApiUrl by viewModel.currentApiUrl.collectAsState()
    val userId by viewModel.userId.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val backendStatus by viewModel.backendStatus.collectAsState()
    val currentHour by viewModel.currentHour.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 60.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.Top
        ) {

            item {
                Text(
                    text = "Admin Panel",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            item {
                AdminSectionTitle("Theme")
                AdminThemeOption(
                    label = "Auto (based on hour)",
                    selected = currentTheme == AppTheme.AUTO,
                    onSelect = { viewModel.setTheme(AppTheme.AUTO) }
                )
                AdminThemeOption(
                    label = "Morning",
                    selected = currentTheme == AppTheme.MORNING,
                    onSelect = { viewModel.setTheme(AppTheme.MORNING) }
                )
                AdminThemeOption(
                    label = "Afternoon",
                    selected = currentTheme == AppTheme.AFTERNOON,
                    onSelect = { viewModel.setTheme(AppTheme.AFTERNOON) }
                )
                AdminThemeOption(
                    label = "Night",
                    selected = currentTheme == AppTheme.NIGHT,
                    onSelect = { viewModel.setTheme(AppTheme.NIGHT) }
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            item {
                AdminSectionTitle("API Base URL")
                AdminApiUrlEditor(
                    currentUrl = currentApiUrl,
                    onApply = { viewModel.setApiUrl(it) },
                    onReset = { viewModel.resetApiUrl() }
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            item {
                AdminSectionTitle("Debug Info")
                AdminDebugRow("User ID", userId)
                AdminDebugRow("Email", userEmail)
                AdminDebugRow("Current hour", currentHour.toString())
                AdminDebugRow("Active theme", currentTheme.name)
                AdminDebugRow("API URL", currentApiUrl)
                AdminDebugRow("Backend", backendStatus)
            }

            item { Spacer(Modifier.height(24.dp)) }

            item {
                AdminSectionTitle("Actions")
                Button(
                    onClick = { viewModel.pingBackend() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text("Ping Backend")
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.logout()
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.Main) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text("Log Out & Clear Tokens")
                }
            }

            item { Spacer(Modifier.height(32.dp)) }

        }
    }
}