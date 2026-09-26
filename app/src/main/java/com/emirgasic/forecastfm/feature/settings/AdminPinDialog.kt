package com.emirgasic.forecastfm.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun AdminPinDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Boolean
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    val forecastColors = LocalForecastColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = forecastColors.card,
        titleContentColor = forecastColors.title,
        textContentColor = forecastColors.body,
        title = {
            Text(
                text = "Admin Access",
                color = forecastColors.title
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter admin PIN",
                    color = forecastColors.body
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        pin = it.take(8)
                        error = false
                    },
                    singleLine = true,
                    isError = error,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = forecastColors.title,
                        unfocusedTextColor = forecastColors.title,
                        focusedBorderColor = forecastColors.primary,
                        unfocusedBorderColor = forecastColors.border,
                        cursorColor = forecastColors.primary
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (onSubmit(pin)) {
                        onDismiss()
                    } else {
                        error = true
                        pin = ""
                    }
                }
            ) {
                Text(
                    text = "Unlock",
                    color = forecastColors.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = forecastColors.muted
                )
            }
        }
    )
}