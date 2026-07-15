package com.emirgasic.forecastfm.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.navigation.Routes
import kotlinx.coroutines.delay
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider

@Composable
fun SplashScreen(navController:NavController,modifier: Modifier=Modifier){

  LaunchedEffect(Unit) {

        delay(2000)

        navController.navigate(Routes.Login) {
            popUpTo(Routes.Splash) {
                inclusive = true
            }
        }

    }



    Box(contentAlignment = Alignment.Center,modifier=modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)){
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier=modifier.fillMaxSize().offset(y=(-120).dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(250.dp)
            )
            Text(text="Forecast FM",color= MaterialTheme.colorScheme.onPrimary,style= MaterialTheme.typography.headlineLarge)
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally ),
                verticalAlignment = Alignment.CenterVertically
            ){
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Text("●",color= MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.width(2.dp))
                    Text(text="Weather",color= MaterialTheme.colorScheme.onBackground,style= MaterialTheme.typography.bodyLarge)

                }
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text("●",color= MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.width(2.dp))
                    Text(text="Music",color= MaterialTheme.colorScheme.onBackground,style= MaterialTheme.typography.bodyLarge)

                }
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text("●",color= MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.width(2.dp))
                    Text(text="Style",color= MaterialTheme.colorScheme.onBackground,style= MaterialTheme.typography.bodyLarge)

                }

            }
        }



        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Loading...",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.40f)
                    .padding(top = 4.dp),
                color = MaterialTheme.colorScheme.primary,
                thickness = 1.dp
            )
        }

    }

}