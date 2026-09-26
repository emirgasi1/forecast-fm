package com.emirgasic.forecastfm.core.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun SectionTitle(
    title: String,
    icon: Painter? = null,
    modifier: Modifier = Modifier,
    style: TextStyle = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
    iconTint: Color = LocalForecastColors.current.title
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = title,
                modifier = Modifier.size(30.dp),
                tint = iconTint
            )
        }

        Text(
            text = title,
            color = LocalForecastColors.current.title,
            style = style
        )
    }
}