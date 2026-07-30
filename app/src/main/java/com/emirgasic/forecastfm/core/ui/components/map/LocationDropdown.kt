package com.emirgasic.forecastfm.core.ui.components.map

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDropdown(
    selectedLocation: String,
    locations: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onLocationSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            onExpandedChange(!expanded)
        },
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)
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
                onExpandedChange(false)
            }
        ) {

            locations.forEach { location ->

                DropdownMenuItem(
                    text = {
                        Text(location)
                    },
                    onClick = {
                        onLocationSelected(location)
                    }
                )

            }

        }

    }

}