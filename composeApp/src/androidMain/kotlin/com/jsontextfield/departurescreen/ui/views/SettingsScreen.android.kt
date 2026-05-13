package com.jsontextfield.departurescreen.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.dynamic_theme
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun DynamicThemeSetting(
    useDynamicTheme: Boolean,
    onDynamicThemeChanged: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Switch(useDynamicTheme, onCheckedChange = onDynamicThemeChanged)
        Text(stringResource(Res.string.dynamic_theme))
    }
}