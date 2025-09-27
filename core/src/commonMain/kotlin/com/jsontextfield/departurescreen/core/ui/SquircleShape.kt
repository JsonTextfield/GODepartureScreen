package com.jsontextfield.departurescreen.core.ui

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin

object SquircleShape : Shape by SquircleShapeImpl()
private class SquircleShapeImpl() : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val n = E
        val path = Path()
        val w = size.width
        val h = size.height
        val steps = 100
        for (i in 0..steps) {
            val theta = (2 * PI * i) / steps
            val x = (w / 2) + (w / 2) * sign(cos(theta)) * abs(cos(theta)).pow(2.0 / n)
            val y = (h / 2) + (h / 2) * sign(sin(theta)) * abs(sin(theta)).pow(2.0 / n)
            if (i == 0) path.moveTo(x.toFloat(), y.toFloat()) else path.lineTo(x.toFloat(), y.toFloat())
        }
        path.close()
        return Outline.Generic(path)
    }
}