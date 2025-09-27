package com.jsontextfield.departurescreen.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.back
import org.jetbrains.compose.resources.stringResource

@Composable
fun BackButton(
    onBackPressed: () -> Unit = {},
) {
    Back(onBackPressed)
    IconButton(onBackPressed) {
        Icon(
            Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(Res.string.back),
        )
    }
}

@Composable
expect fun Back(onBackPressed: () -> Unit)