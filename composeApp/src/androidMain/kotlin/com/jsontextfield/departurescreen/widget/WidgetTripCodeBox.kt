package com.jsontextfield.departurescreen.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import androidx.glance.unit.ColorProvider

@Composable
fun WidgetTripCodeBox(
    tripCode: String,
    modifier: GlanceModifier = GlanceModifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = tripCode,
            style = TextDefaults.defaultTextStyle.copy(
                color = ColorProvider(Color.White),
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}