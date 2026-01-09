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
        val n = 2.0 / E
        val path = Path()
        val side = size.width
        val radius = side / 2
        val steps = 100
        for (i in 0..steps) {
            val theta = (2 * PI * i) / steps
            val cosTheta = cos(theta)
            val sinTheta = sin(theta)
            val x = (radius * sign(cosTheta) * abs(cosTheta).pow(n) + radius).toFloat()
            val y = (radius * sign(sinTheta) * abs(sinTheta).pow(n) + radius).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return Outline.Generic(path)
    }
}