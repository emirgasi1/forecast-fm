package com.emirgasic.forecastfm.feature.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.auth.AuthButton
import com.emirgasic.forecastfm.core.ui.components.auth.EmailField
import com.emirgasic.forecastfm.core.ui.components.auth.PasswordField
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun RegisterScreen(
    navController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier
) {
    val forecastColors = LocalForecastColors.current

    val registerViewModel: RegisterViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return RegisterViewModel(
                    tokenManager = tokenManager,
                    onRegisterSuccess = {
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.Register) { inclusive = true }
                        }
                    }
                ) as T
            }
        }
    )

    val username by registerViewModel.username.collectAsState()
    val email by registerViewModel.email.collectAsState()
    val password by registerViewModel.password.collectAsState()
    val confirmPassword by registerViewModel.confirmPassword.collectAsState()
    val checkMark by registerViewModel.checkMark.collectAsState()
    val isLoading by registerViewModel.isLoading.collectAsState()
    val errorMessage by registerViewModel.errorMessage.collectAsState()

    val scrollState = rememberScrollState()

    Box(modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(250.dp)
            )
            Text(
                text = "Create Account",
                color = forecastColors.title,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.offset(y = (-30).dp)
            )
            Text(
                text = "Join the Bascarsija vibe today",
                color = forecastColors.body,
                style = MaterialTheme.typography.titleLarge,
                modifier = modifier.width(320.dp)
            )

            Spacer(modifier = modifier.height(14.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier.width(320.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Text(
                    text = "Username",
                    color = forecastColors.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = modifier.align(Alignment.Start)
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { registerViewModel.updateUsername(it) },
                    label = { Text("User78") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(modifier = modifier.height(14.dp))

                Text(
                    text = "Email",
                    color = forecastColors.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )
                EmailField(
                    email = email,
                    onEmailChange = { registerViewModel.updateEmail(it) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = modifier.height(14.dp))

                Text(
                    text = "Password",
                    color = forecastColors.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )
                PasswordField(
                    password = password,
                    onPasswordChange = { registerViewModel.updatePassword(it) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = modifier.height(14.dp))

                Text(
                    text = "Confirm Password",
                    color = forecastColors.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )
                PasswordField(
                    password = confirmPassword,
                    onPasswordChange = { registerViewModel.updateConfirmPassword(it) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checkMark,
                        onCheckedChange = { registerViewModel.updateCheckMark(it) },
                        enabled = !isLoading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = forecastColors.muted,
                            checkmarkColor = MaterialTheme.colorScheme.background
                        )
                    )
                    Text(
                        text = "I Agree to the Terms & Privacy",
                        modifier = Modifier.padding(start = 8.dp),
                        color = forecastColors.body,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = modifier.height(20.dp))

                AuthButton(
                    text = if (isLoading) "Creating Account..." else "Create Account",
                    onClick = { registerViewModel.register() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = forecastColors.border
                    )
                    Text(
                        text = " OR ",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = forecastColors.muted
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = forecastColors.border
                    )
                }
                Spacer(modifier.height(20.dp))
                Text(
                    text = "Continue with Google",
                    color = forecastColors.body,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier.height(12.dp))
                Text(
                    text = "Log In",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = modifier.clickable { navController.navigate(Routes.Login) }
                )
                Spacer(modifier.height(32.dp))
            }
        }
    }
}