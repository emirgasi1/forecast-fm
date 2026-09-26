package com.emirgasic.forecastfm.core.ui.components.place

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun PlaceInfoHeader(
    name: String,
    category: String,
    rating: Double,
    isSaved: Boolean,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val forecastColors = LocalForecastColors.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "← Back",
            style = MaterialTheme.typography.titleMedium,
            color = forecastColors.title,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .clickable { onBackClick() }
        )

        Icon(
            painter = painterResource(
                if (isSaved) R.drawable.bookmark_filled else R.drawable.bookmark
            ),
            contentDescription = if (isSaved) "Unsave" else "Save",
            modifier = Modifier
                .size(28.dp)
                .clickable { onSaveClick() },
            tint = forecastColors.primary
        )
    }
}