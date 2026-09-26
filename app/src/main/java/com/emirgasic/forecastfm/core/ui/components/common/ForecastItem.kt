package com.emirgasic.forecastfm.core.ui.components.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun ForecastItem(
    icon: Int,
    day: String,
    temperature: String,
    modifier: Modifier = Modifier
){

    val forecastColors = LocalForecastColors.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Image(
            painter = painterResource(icon),
            contentDescription = day,
            modifier = Modifier.size(30.dp)
        )

        Text(
            text = day,
            color = forecastColors.muted,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = temperature,
            color = forecastColors.title,
            style = MaterialTheme.typography.titleLarge
        )
    }
}