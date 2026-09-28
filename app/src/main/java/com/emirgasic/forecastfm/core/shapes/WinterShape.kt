package com.emirgasic.forecastfm.core.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class WinterShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val w = size.width
        val h = size.height
        val inset = w * 0.16f
        val insetY = h * 0.16f

        val path = Path().apply {
            moveTo(inset, insetY * 0.5f)
            lineTo(w - inset, insetY * 0.5f)
            lineTo(w, h / 2f)
            lineTo(w - inset, h - insetY * 0.5f)
            lineTo(inset, h - insetY * 0.5f)
            lineTo(0f, h / 2f)
            close()
        }
        return Outline.Generic(path)
    }
}