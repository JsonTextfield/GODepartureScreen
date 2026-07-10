package com.jsontextfield.departurescreen.ui.views.tripdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.ui.components.AlertItem
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.alerts
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlertsSection(alerts: List<Alert>, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SectionHeader(stringResource(Res.string.alerts))
        alerts.forEach { alert ->
            AlertItem(
                alert = alert,
                onClick = {
                    if ("fr" in Locale.current.language) {
                        alert.urlFr
                    } else {
                        alert.urlEn
                    }?.let(uriHandler::openUri)
                },
            )
        }
    }
}