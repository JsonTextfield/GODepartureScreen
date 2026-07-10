package com.jsontextfield.departurescreen.ui.views.tripdetails

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
fun Modifier.sectionBorder(): Modifier {
    return this.border(
        1.dp,
        MaterialTheme.colorScheme.outline.copy(alpha = .5f),
        RoundedCornerShape(8.dp)
    )
}