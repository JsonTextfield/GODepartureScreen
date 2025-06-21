package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.app_name_long
import departure_screen.composeapp.generated.resources.close
import departure_screen.composeapp.generated.resources.licences
import org.jetbrains.compose.resources.stringResource


@Composable
fun AboutDialog(
    onLicences: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(Res.string.app_name_long))
        },
        text = {
            //Text(text = stringResource(Res.string.version, BuildConfig.VERSION_NAME))
        },
        confirmButton = {
            Row {
                TextButton(onClick = {
                    onLicences()
                    onDismiss()
                }) {
                    Text(stringResource(Res.string.licences))
                }
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(Res.string.close))
                }
            }
        }
    )
}