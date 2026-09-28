package com.emirgasic.forecastfm.core.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class AutumnShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        val path = Path().apply {
            moveTo(cx, 0f)
            cubicTo(
                w * 0.78f, 0f,
                w, h * 0.24f,
                w, cy
            )
            cubicTo(
                w, h * 0.76f,
                w * 0.78f, h,
                cx, h
            )
            cubicTo(
                w * 0.22f, h,
                0f, h * 0.76f,
                0f, cy
            )
            cubicTo(
                0f, h * 0.24f,
                w * 0.22f, 0f,
                cx, 0f
            )
            close()
        }
        return Outline.Generic(path)
    }
}