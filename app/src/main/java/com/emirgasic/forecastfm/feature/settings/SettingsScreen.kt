package com.emirgasic.forecastfm.feature.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.settings.SettingsOptionCard
import com.emirgasic.forecastfm.core.ui.components.settings.SettingsSection


@Composable
fun SettingsScreen(navController: NavController,modifier: Modifier =Modifier){

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 60.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    ){
        LazyColumn(modifier=Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start){

            item{
                Text(text="Settings",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineMedium)
            }
            item{
                Spacer(modifier.height(28.dp))
            }
            item {
                SettingsSection(
                    title = "Account"
                ) {

                    SettingsOptionCard(
                        title = "Edit Profile",
                        onClick = {
                            navController.navigate(Routes.EditProfile)
                        }
                    )

                }
            }
            item{
                Spacer(modifier.height(24.dp))
            }

            item{
                SettingsSection(
                    title = "Preferences"
                ) {

                    SettingsOptionCard(
                        title = "Default Location"
                    )

                    SettingsOptionCard(
                        title = "Theme"
                    )

                    SettingsOptionCard(
                        title = "Notifications"
                    )

                }
            }


            item{
                Spacer(modifier.height(24.dp))
            }

            item{
                SettingsSection(
                    title = "About"
                ) {

                    SettingsOptionCard(
                        title = "Privacy Policy"
                    )

                    SettingsOptionCard(
                        title = "About App"
                    )

                }
            }
            item{
                Spacer(modifier.height(20.dp))
            }
           item{
               Button(onClick = {}) {
                   Text(text="View Details")
               }
           }
        }
    }

}