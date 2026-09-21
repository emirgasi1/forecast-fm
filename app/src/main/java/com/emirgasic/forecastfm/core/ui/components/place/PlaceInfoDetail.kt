package com.emirgasic.forecastfm.core.ui.components.place

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirgasic.forecastfm.core.ui.components.common.DetailRow

@Composable
fun PlaceInfoDetails(
    address: String,
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(20.dp)
    ) {
        Text(
            text = "Details",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        DetailRow(
            label = "Address",
            value = if (address.isNotBlank()) address else "Address not available",
            labelSize = 16.sp,
            valueSize = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        DetailRow(
            label = "Coordinates",
            value = "$latitude, $longitude",
            labelSize = 16.sp,
            valueSize = 16.sp
        )
    }
}