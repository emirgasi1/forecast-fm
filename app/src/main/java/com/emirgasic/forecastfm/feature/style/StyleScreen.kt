package com.emirgasic.forecastfm.feature.style

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
@Composable
fun StyleScreen(navController: NavController,modifier: Modifier=Modifier){
    val outfits = listOf(
        "Coffee Morning",
        "Sarajevo Casual",
        "Rainy Walk",
        "Night Out"
    )
    Box(modifier=Modifier.fillMaxSize()
        .background(color = MaterialTheme.colorScheme.background)
        .padding(top = 20.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)){
        Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start){
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top){
                Image(painter = painterResource(R.drawable.clothes), contentDescription = "Style",modifier = Modifier.size(30.dp))
                Text(text="Style",
                    color= MaterialTheme.colorScheme.onBackground,
                    style= MaterialTheme.typography.headlineSmall)
            }
            Spacer(modifier.height(20.dp))
            Text(text="Outfits inspired by Sarajevo weather",color= MaterialTheme.colorScheme.onBackground)
            Spacer(modifier.height(20.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                items(outfits) { outfit ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.8f),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(
                                        MaterialTheme.colorScheme.surface
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = "Outfit Image",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                text = outfit,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "18°C • Morning",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.7f
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}