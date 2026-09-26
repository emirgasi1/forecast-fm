package com.emirgasic.forecastfm.core.ui.components.style.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    title: String,
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier
    ) {

        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    text = title,
                    color = forecastColors.muted
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = forecastColors.card,
                unfocusedContainerColor = forecastColors.card,
                focusedTextColor = forecastColors.title,
                unfocusedTextColor = forecastColors.title,
                focusedBorderColor = forecastColors.primary,
                unfocusedBorderColor = forecastColors.border,
                focusedPlaceholderColor = forecastColors.muted,
                unfocusedPlaceholderColor = forecastColors.muted,
                focusedTrailingIconColor = forecastColors.muted,
                unfocusedTrailingIconColor = forecastColors.muted
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.background(forecastColors.card)
        ) {

            options.forEach { option ->

                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = forecastColors.title
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}