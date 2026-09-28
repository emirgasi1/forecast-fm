package com.emirgasic.forecastfm.core.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class SpringShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        val path = Path().apply {
            moveTo(cx, 0f)
            cubicTo(
                w * 0.85f, 0f,
                w, h * 0.35f,
                w, h * 0.60f
            )
            cubicTo(
                w, h * 0.88f,
                w * 0.78f, h,
                cx, h
            )
            cubicTo(
                w * 0.22f, h,
                0f, h * 0.88f,
                0f, h * 0.60f
            )
            cubicTo(
                0f, h * 0.35f,
                w * 0.15f, 0f,
                cx, 0f
            )
            close()
        }
        return Outline.Generic(path)
    }
}