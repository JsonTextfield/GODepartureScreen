package com.jsontextfield.departurescreen.ui.views.tripdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.components.AlertItem
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.alerts
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlertsSection(
    alerts: List<Alert>,
    status: Status,
    modifier: Modifier = Modifier,
    onAlertClicked: (String) -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if ((status == Status.LOADING) || (status == Status.LOADED && alerts.isNotEmpty())) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SectionHeader(stringResource(Res.string.alerts))
                if (status == Status.LOADING) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                }
            }
        }
        if (alerts.isNotEmpty()) {
            alerts.forEach { alert ->
                AlertItem(
                    alert = alert,
                    onClick = { onAlertClicked(alert.id) },
                )
            }
        }
    }
}