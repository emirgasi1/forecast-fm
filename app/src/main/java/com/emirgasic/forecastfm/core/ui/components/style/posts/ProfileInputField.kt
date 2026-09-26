package com.emirgasic.forecastfm.core.ui.components.style.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun ProfileInputField(
    title: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {

    val forecastColors = LocalForecastColors.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Text(
            text = title,
            color = forecastColors.title,
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = forecastColors.muted
                )
            },
            singleLine = singleLine,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = forecastColors.card,
                unfocusedContainerColor = forecastColors.card,
                focusedTextColor = forecastColors.title,
                unfocusedTextColor = forecastColors.title,
                focusedBorderColor = forecastColors.primary,
                unfocusedBorderColor = forecastColors.border,
                focusedPlaceholderColor = forecastColors.muted,
                unfocusedPlaceholderColor = forecastColors.muted,
                cursorColor = forecastColors.primary
            )
        )
    }
}