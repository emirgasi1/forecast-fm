package com.emirgasic.forecastfm.feature.auth.login

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.auth.AuthButton
import com.emirgasic.forecastfm.core.ui.components.auth.EmailField
import com.emirgasic.forecastfm.core.ui.components.auth.PasswordField

@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
){
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()


    Box(modifier.fillMaxSize()){
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(250.dp)
            )
            Text(text="Welcome Back",color= MaterialTheme.colorScheme.onPrimary,style= MaterialTheme.typography.headlineLarge,modifier = Modifier.offset(y = (-30).dp))
            Spacer(modifier=modifier.height(24.dp))
            Text(text="Continue your Sarajevo vibe.",color= MaterialTheme.colorScheme.onBackground,style= MaterialTheme.typography.titleLarge,modifier=modifier.width(320.dp))

            Spacer(modifier=modifier.height(24.dp))
            Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = modifier.width(320.dp)){
                Text(
                    text="Email",
                    color= MaterialTheme.colorScheme.onPrimary,
                    style= MaterialTheme.typography.titleMedium,
                    modifier=modifier.align(Alignment.Start)
                )

                EmailField(
                    email = email,
                    onEmailChange = {
                        viewModel.updateEmail(it)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier.height(34.dp))

                PasswordField(
                    password = password,
                    onPasswordChange = {
                        viewModel.updatePassword(it)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier.height(32.dp))

                AuthButton(
                    text = "Log In",
                    onClick = {
                        viewModel.login()
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier.height(32.dp))

                Text(text="Forgot password?",color= MaterialTheme.colorScheme.onBackground,style=MaterialTheme.typography.titleMedium,modifier=modifier.clickable{navController.navigate(Routes.ForgotPassword)})
                Spacer(modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outline
                    )

                    Text(
                        text = " OR ",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(modifier.height(20.dp))
                Text(text="Continue with Google",color= MaterialTheme.colorScheme.onBackground,style=MaterialTheme.typography.titleMedium)
                Spacer(modifier.height(12.dp))
                Text(text="Sign Up",color= MaterialTheme.colorScheme.onBackground,style=MaterialTheme.typography.titleMedium,modifier=modifier.clickable{navController.navigate(Routes.Register)})
                Spacer(modifier.height(32.dp))
            }
        }



    }
}