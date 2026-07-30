package com.emirgasic.forecastfm.feature.map

import android.R.attr.onClick
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.emirgasic.forecastfm.core.ui.components.common.ScreenTitle
import com.emirgasic.forecastfm.core.ui.components.map.LocationDropdown
import com.emirgasic.forecastfm.core.ui.components.map.LocationRecommendationCard
import com.emirgasic.forecastfm.core.ui.components.map.MapPreviewCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController,modifier: Modifier =Modifier){
    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedLocation by remember {
        mutableStateOf("Baščaršija")
    }

    val locations = listOf(
        "Baščaršija",
        "Ilidža",
        "Trebević",
        "Dobrinja",
        "Grbavica"
    )
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 20.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ){
            ScreenTitle(
                title = "Map",
                icon = painterResource(R.drawable.mappin)
            )
            Spacer(modifier.height(16.dp))
            MapPreviewCard(
                icon = painterResource(R.drawable.mappin)
            )
            Spacer(modifier.height(16.dp))


            LocationDropdown(
                selectedLocation = selectedLocation,
                locations = locations,
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                },
                onLocationSelected = {
                    selectedLocation = it
                    expanded = false
                }
            )
            Spacer(modifier.height(18.dp))
            LocationRecommendationCard(
                location = "Baščaršija",
                weatherIcon = painterResource(R.drawable.sun),
                temperature = "22°C",
                weather = "Sunny",
                music = "Coffee House Vibes",
                outfit = "Light Jacket + Jeans",
                onViewDetailsClick = {
                    // Navigate later
                }
            )
        }
    }
}