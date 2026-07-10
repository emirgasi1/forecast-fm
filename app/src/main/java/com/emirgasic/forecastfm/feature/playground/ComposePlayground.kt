package com.emirgasic.forecastfm.feature.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement

@Composable
fun ComposePlayground(modifier:Modifier=Modifier){
    Column(
        modifier=modifier.fillMaxSize(),
        verticalArrangement =Arrangement.Center,
        horizontalAlignment=Alignment.CenterHorizontally
    ){

        Text("The app Font")

    }
}

