package com.emirgasic.forecastfm.core.ui.components.feed

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun ActionIcon(
    painter: Painter,
    tint: Color,
    count: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bounceTrigger: Boolean = false,
    bounceOnTap: Boolean = false
) {

    val forecastColors = LocalForecastColors.current

    val scale = remember { Animatable(1f) }
    var isFirstComposition by remember { mutableStateOf(true) }
    var tapCounter by remember { mutableIntStateOf(0) }

    // Bounce on state flip (heart: unliked -> liked)
    LaunchedEffect(bounceTrigger) {
        if (isFirstComposition) {
            isFirstComposition = false
            return@LaunchedEffect
        }
        if (bounceTrigger) {
            scale.animateTo(
                targetValue = 1.35f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    // Bounce on tap (comment, bookmark, any action without a boolean state)
    LaunchedEffect(tapCounter) {
        if (bounceOnTap && tapCounter > 0) {
            scale.animateTo(
                targetValue = 1.35f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    Column(
        modifier = modifier.clickable {
            if (bounceOnTap) tapCounter++
            onClick()
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(forecastColors.background.copy(alpha = 0.55f))
                .border(
                    width = 1.dp,
                    color = forecastColors.border.copy(alpha = 0.35f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(22.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    },
                tint = tint
            )
        }

        if (count != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}