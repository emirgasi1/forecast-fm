package com.emirgasic.forecastfm.feature.home

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R

@Composable
fun HomeScreen(navController: NavController,modifier:Modifier= Modifier){
    Box(modifier=modifier.fillMaxSize().background(color=MaterialTheme.colorScheme.background)){
        LazyColumn(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Top,modifier=modifier.fillMaxSize().padding(20.dp)) {
            item {
                Spacer(modifier=Modifier.height(40.dp))
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier=Modifier.fillMaxWidth()
                ) {
                    Text(text = "Good Morning",color= MaterialTheme.colorScheme.onBackground,style=MaterialTheme.typography.titleSmall)
                    Spacer(modifier=Modifier.width(6.dp))
                    Image(painter = painterResource(R.drawable.sun), contentDescription = "Sunny",modifier=modifier.size(24.dp))
                }
                Spacer(modifier=Modifier.height(10.dp))
                Text(text = "Bascarsija",color= MaterialTheme.colorScheme.onPrimary,style=MaterialTheme.typography.headlineMedium)
                Card() { }
            }
        }
    }
}