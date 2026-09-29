package com.emirgasic.forecastfm.feature.auth.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.auth.AuthButton
import com.emirgasic.forecastfm.core.ui.components.auth.EmailField
import com.emirgasic.forecastfm.data.repository.AuthRepository
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val forecastColors = LocalForecastColors.current

    val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ForgotPasswordViewModel(
                    authRepository = AuthRepository(),
                    onSuccess = {
                        navController.popBackStack()
                    }
                ) as T
            }
        }
    )

    val email by forgotPasswordViewModel.email.collectAsState()
    val isLoading by forgotPasswordViewModel.isLoading.collectAsState()
    val errorMessage by forgotPasswordViewModel.errorMessage.collectAsState()
    val successMessage by forgotPasswordViewModel.successMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineLarge,
            color = forecastColors.title
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter your email and we'll send you a reset link",
            style = MaterialTheme.typography.bodyMedium,
            color = forecastColors.body
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (successMessage != null) {
            Text(
                text = successMessage ?: "",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        EmailField(
            email = email,
            onEmailChange = { forgotPasswordViewModel.updateEmail(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthButton(
            text = if (isLoading) "Sending..." else "Send Reset Link",
            onClick = { forgotPasswordViewModel.sendResetLink() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Back to Login",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.clickable { navController.popBackStack() }
        )
    }
}