package com.jsontextfield.departurescreen.widget.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import com.jsontextfield.departurescreen.R

@Composable
fun WidgetTripListHeaderRow(modifier: GlanceModifier = GlanceModifier) {
    val context = LocalContext.current
    Row(modifier = modifier) {
        Text(
            text = context.getString(R.string.time),
            style = TextDefaults.defaultTextStyle.copy(
                fontSize = 12.sp,
                color = GlanceTheme.colors.onBackground,
                textAlign = TextAlign.Center,
            ),
            modifier = GlanceModifier.width(40.dp)
        )
        Spacer(modifier = GlanceModifier.width(12.dp))
        Text(
            text = context.getString(R.string.line),
            style = TextDefaults.defaultTextStyle.copy(
                fontSize = 12.sp,
                color = GlanceTheme.colors.onBackground,
                textAlign = TextAlign.Center,
            ),
            modifier = GlanceModifier.defaultWeight()
        )
        Spacer(modifier = GlanceModifier.width(12.dp))
        Text(
            text = context.getString(R.string.platform),
            style = TextDefaults.defaultTextStyle.copy(
                fontSize = 12.sp,
                color = GlanceTheme.colors.onBackground,
                textAlign = TextAlign.Center,
            ),
            modifier = GlanceModifier.width(60.dp)
        )
    }
}