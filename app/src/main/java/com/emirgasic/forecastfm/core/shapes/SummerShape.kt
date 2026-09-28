package com.emirgasic.forecastfm.core.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class SummerShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val w = size.width
        val h = size.height
        val cut = w * 0.18f

        val path = Path().apply {
            moveTo(cut, 0f)
            lineTo(w - cut, 0f)
            cubicTo(
                w - cut * 0.25f, 0f,
                w, cut * 0.25f,
                w, cut
            )
            lineTo(w, h - cut)
            cubicTo(
                w, h - cut * 0.25f,
                w - cut * 0.25f, h,
                w - cut, h
            )
            lineTo(cut, h)
            cubicTo(
                cut * 0.25f, h,
                0f, h - cut * 0.25f,
                0f, h - cut
            )
            lineTo(0f, cut)
            cubicTo(
                0f, cut * 0.25f,
                cut * 0.25f, 0f,
                cut, 0f
            )
            close()
        }
        return Outline.Generic(path)
    }
}