package com.emirgasic.forecastfm.feature.music.musichistory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
fun MusicHistoryScreen(navController: NavController,modifier: Modifier =Modifier) {
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 60.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    )
    {
        LazyColumn(modifier.fillMaxWidth(),verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {

            item{
                Text(text="Today",color= MaterialTheme.colorScheme.onPrimary,style= MaterialTheme.typography.headlineMedium)
            }
            item{
                Spacer(modifier.height(16.dp))
            }
            item{
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )) {
                    Column(modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.surfaceVariant).padding(16.dp),horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(20.dp),) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                            Image(painter = painterResource(R.drawable.music),
                                contentDescription = "Music",
                                Modifier.size(20.dp))
                            Text(text="Morning Coffee Jazz",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                            Image(painter = painterResource(R.drawable.sun),
                                contentDescription = "Weather",
                                Modifier.size(20.dp))
                            Text(text="Sunny",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                            Text(text="12°C",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                            Text(text="Bascarsija",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        }
                        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Top) {
                            Text(text="08:34 AM",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }


            item{
                Spacer(modifier.height(16.dp))
            }
            item{
                Text(text="Today",color= MaterialTheme.colorScheme.onPrimary,style= MaterialTheme.typography.headlineMedium)
            }
            item{
                Spacer(modifier.height(16.dp))
            }
            item{
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )) {
                    Column(modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.surfaceVariant).padding(16.dp),horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(20.dp),) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                            Image(painter = painterResource(R.drawable.music),
                                contentDescription = "Music",
                                Modifier.size(20.dp))
                            Text(text="Morning Coffee Jazz",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                            Image(painter = painterResource(R.drawable.sun),
                                contentDescription = "Weather",
                                Modifier.size(20.dp))
                            Text(text="Sunny",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                            Text(text="12°C",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                            Text(text="Bascarsija",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        }
                        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Top) {
                            Text(text="08:34 AM",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }

            item{
                Spacer(modifier.height(16.dp))
            }
            item{
                Text(text="Today",color= MaterialTheme.colorScheme.onPrimary,style= MaterialTheme.typography.headlineMedium)
            }
            item{
                Spacer(modifier.height(16.dp))
            }
            item{
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )) {
                    Column(modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.surfaceVariant).padding(16.dp),horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(20.dp),) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                            Image(painter = painterResource(R.drawable.music),
                                contentDescription = "Music",
                                Modifier.size(20.dp))
                            Text(text="Morning Coffee Jazz",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                            Image(painter = painterResource(R.drawable.sun),
                                contentDescription = "Weather",
                                Modifier.size(20.dp))
                            Text(text="Sunny",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                            Text(text="12°C",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                            Text(text="Bascarsija",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        }
                        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Top) {
                            Text(text="08:34 AM",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }

            item{
                Spacer(modifier.height(16.dp))
            }
            item{
                Text(text="Today",color= MaterialTheme.colorScheme.onPrimary,style= MaterialTheme.typography.headlineMedium)
            }
            item{
                Spacer(modifier.height(16.dp))
            }
            item{
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )) {
                    Column(modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.surfaceVariant).padding(16.dp),horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(20.dp),) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                            Image(painter = painterResource(R.drawable.music),
                                contentDescription = "Music",
                                Modifier.size(20.dp))
                            Text(text="Morning Coffee Jazz",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                            Image(painter = painterResource(R.drawable.sun),
                                contentDescription = "Weather",
                                Modifier.size(20.dp))
                            Text(text="Sunny",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                            Text(text="12°C",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                            Text(text="Bascarsija",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        }
                        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Top) {
                            Text(text="08:34 AM",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }

    }
}