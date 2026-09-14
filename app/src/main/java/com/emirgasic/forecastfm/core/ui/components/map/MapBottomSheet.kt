package com.emirgasic.forecastfm.core.ui.components.map

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.core.ui.components.map.MapBottomSheetTabs
import com.emirgasic.forecastfm.core.ui.components.map.MapTab
import com.emirgasic.forecastfm.core.ui.components.map.tabs.FiltersTabContent
import com.emirgasic.forecastfm.core.ui.components.map.tabs.LayersTabContent
import com.emirgasic.forecastfm.core.ui.components.map.tabs.LegendTabContent
import com.emirgasic.forecastfm.core.ui.components.map.tabs.SearchTabContent

@Composable
fun MapBottomSheet(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val expandedHeight = screenHeight * 0.5f
    val collapsedHeight = 40.dp

    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isExpanded by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(MapTab.Search) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }
    var enabledLayers by remember { mutableStateOf(setOf("Venues", "Places")) }

    val maxDrag = (expandedHeight - collapsedHeight).value
    val baseOffset = if (isExpanded) 0f else maxDrag

    val animatedOffset by animateFloatAsState(
        targetValue = (baseOffset + dragOffset).coerceIn(0f, maxDrag),
        animationSpec = tween(durationMillis = 200),
        label = "sheetOffset"
    )

    val currentHeight = expandedHeight

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(currentHeight)
            .offset(y = animatedOffset.dp)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                if (dragOffset > maxDrag * 0.3f) {
                                    isExpanded = false
                                } else if (dragOffset < -maxDrag * 0.3f) {
                                    isExpanded = true
                                }
                                dragOffset = 0f
                            },
                            onDragCancel = {
                                dragOffset = 0f
                            },
                            onVerticalDrag = { _, dragAmount ->
                                dragOffset += dragAmount
                                dragOffset = dragOffset.coerceIn(-maxDrag, maxDrag)
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                content()
            }
        }
    }
}