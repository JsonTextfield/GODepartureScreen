package com.jsontextfield.departurescreen.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.ui.SquircleShape
import com.jsontextfield.departurescreen.core.ui.components.BackButton
import com.jsontextfield.departurescreen.core.ui.components.TripCodeBox
import com.jsontextfield.departurescreen.core.ui.theme.lineColours

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen(
    alert: Alert,
    onBackPressed: () -> Unit = {},
) {
    val language = Locale.current.language
    val fontScale = LocalDensity.current.fontScale
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Alert details")
            }, navigationIcon = {
                BackButton(onBackPressed)
            })
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            Text(alert.getSubject(language))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (line in alert.affectedLines) {
                    TripCodeBox(
                        tripCode = line,
                        modifier = Modifier
                            .size((MaterialTheme.typography.titleMedium.fontSize.value * fontScale * 2).dp)
                            .background(
                                color = lineColours[line] ?: Color.Gray,
                                shape = SquircleShape,
                            )
                    )
                }
            }
            Text(alert.getBody(language))
        }
    }
}