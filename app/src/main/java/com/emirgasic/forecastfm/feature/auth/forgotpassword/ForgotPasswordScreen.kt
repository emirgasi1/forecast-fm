package com.emirgasic.forecastfm.feature.auth.forgotpassword

import android.R.attr.value
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.navigation.Routes

@Composable
fun ForgotPasswordScreen(navController: NavController,modifier:Modifier=Modifier){
    var email by remember{
        mutableStateOf("")
    }
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,modifier=modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
            Image(painter = painterResource(R.drawable.lock,), contentDescription = "Lock Logo",modifier = Modifier.size(100.dp))
            Spacer(modifier.height(52.dp))
            Text(text="Reset Your Password",color=MaterialTheme.colorScheme.onPrimary, style=MaterialTheme.typography.headlineLarge,modifier = Modifier.offset(y = (-30).dp))
            Text(text="Enter your email associated with your account",color=MaterialTheme.colorScheme.onBackground,style=MaterialTheme.typography.titleLarge,modifier=modifier.width(320.dp))
            Spacer(modifier=modifier.height(30.dp))
            Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center, modifier = modifier.width(320.dp)){
                Text(text="Email",color= MaterialTheme.colorScheme.onPrimary,style= MaterialTheme.typography.headlineSmall)
                Spacer(modifier.width(24.dp))
                OutlinedTextField(value=email,
                    onValueChange = {newEmail->
                        email=newEmail}
                    ,label={
                        Text("example@gmail.com")
                           },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(modifier=modifier.height(32.dp))
                Button(
                    onClick = {

                    },modifier=modifier.fillMaxWidth().height(54.dp),
                    shape=RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.background
                    ))
                {
                    Text(text="Log In"  ,style=MaterialTheme.typography.titleLarge)
                }
                Spacer(modifier.height(32.dp))
                Text(text="Back to Login",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleSmall,modifier=modifier.clickable{navController.navigate(Routes.Login)})
            }
        }
    }
}