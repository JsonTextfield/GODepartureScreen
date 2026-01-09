package com.jsontextfield.departurescreen.widget.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import com.jsontextfield.departurescreen.R

@Composable
fun RefreshButton(
    title: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
    ) {
        Image(
            ImageProvider(R.drawable.refresh_24px),
            null,
            colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground)
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        Text(
            text = title,
            style = TextDefaults.defaultTextStyle.copy(
                textAlign = TextAlign.Center,
                color = GlanceTheme.colors.onBackground,
            ),
        )
    }
}