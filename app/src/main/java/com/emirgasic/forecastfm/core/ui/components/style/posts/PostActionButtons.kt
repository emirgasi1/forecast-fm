package com.emirgasic.forecastfm.core.ui.components.style.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun PostActionButtons(
    onPostClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onPostClick,
            modifier = Modifier.weight(1f),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = forecastColors.primary,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Text(if (isLoading) "Posting..." else "Post")
        }

        Button(
            onClick = onDeleteClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = forecastColors.error,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Text("Delete")
        }
    }
}