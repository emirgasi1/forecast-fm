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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(R.drawable.mappin),
                    contentDescription = "Pin",
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "Map",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.mappin),
                            contentDescription = "Map",
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Interactive Map",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Google Maps",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier.height(16.dp))


            ExposedDropdownMenuBox(

                expanded = expanded,

                onExpandedChange = {
                    expanded = !expanded
                } ,modifier=Modifier.background(color= MaterialTheme.colorScheme.surfaceVariant)
            ) {

                OutlinedTextField(

                    value = selectedLocation,

                    onValueChange = {},

                    readOnly = true,

                    label = {
                        Text("Location")
                    },

                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },

                    modifier = Modifier.menuAnchor()

                )

                ExposedDropdownMenu(

                    expanded = expanded,

                    onDismissRequest = {
                        expanded = false
                    },


                ) {
                    locations.forEach { location ->

                        DropdownMenuItem(

                            text = {
                                Text(location)
                            },

                            onClick = {

                                selectedLocation = location
                                expanded = false

                            }
                        )

                    }

                }

            }
            Spacer(modifier.height(18.dp))
            Card(modifier=Modifier.fillMaxWidth(),
                shape= MaterialTheme.shapes.medium,
                border= BorderStroke(width=1.dp,MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )) {
                Column(modifier=Modifier.fillMaxWidth().padding(14.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Row(horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically){
                            Image(painter=painterResource(R.drawable.mappin), contentDescription = "Location",modifier.size(20.dp))
                            Spacer(modifier.width(8.dp))
                            Text(text="Bascarsija",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.bodyLarge)
                    }
                    Row(horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically){
                        Image(painter=painterResource(R.drawable.sun), contentDescription = "Sun",modifier.size(20.dp))
                        Spacer(modifier.width(8.dp))
                        Text(text="22°C",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.bodyLarge)
                        Spacer(modifier.width(8.dp))
                        Text(text="•",color= MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.3f),style= MaterialTheme.typography.bodyLarge)
                        Spacer(modifier.width(8.dp))
                        Text(text="Sunny",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.bodyLarge)
                    }
                    Row(horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically){
                        Image(painter=painterResource(R.drawable.music), contentDescription = "Music",modifier.size(20.dp))
                        Spacer(modifier.width(8.dp))
                        Text(text="Coffee House Vibes",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.bodyLarge)
                    }
                    Row(horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically){
                        Image(painter=painterResource(R.drawable.clothes), contentDescription = "Style",modifier.size(20.dp))
                        Spacer(modifier.width(8.dp))
                        Text(text="Light Jacket + Jeans",color= MaterialTheme.colorScheme.onSurfaceVariant,style= MaterialTheme.typography.bodyLarge)
                    }
                    Button(onClick = {}) {
                        Text(text="View Details")
                    }
                }
            }
        }
    }
}