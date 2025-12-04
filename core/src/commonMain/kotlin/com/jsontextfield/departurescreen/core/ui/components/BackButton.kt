package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.back
import departure_screen.core.generated.resources.rounded_arrow_back_24
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BackButton(
    onBackPressed: () -> Unit = {},
) {
    IconButton(onBackPressed) {
        Icon(
            painterResource(Res.drawable.rounded_arrow_back_24),
            contentDescription = stringResource(Res.string.back),
        )
    }
}