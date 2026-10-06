package com.emirgasic.forecastfm.core.ui.components.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.core.navigation.BottomBarItem
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun BottomBarItemContent(
    item: BottomBarItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val forecastColors = LocalForecastColors.current

    val iconColor by animateColorAsState(
        targetValue = if (selected) forecastColors.title else forecastColors.muted,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "bottomBarIconColor"
    )

    // Icon scale lags behind the circle's travel by ~100ms so it "pops"
    // as the circle arrives rather than popping in place immediately.
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.14f else 1f,
        animationSpec = tween(
            durationMillis = 180,
            delayMillis = 80,
            easing = FastOutSlowInEasing
        ),
        label = "bottomBarIconScale"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(if (selected) item.iconFilled else item.iconOutlined),
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier
                .size(28.dp)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                }
        )
    }
}