package com.jsontextfield.departurescreen.widget.config

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.R
import com.jsontextfield.departurescreen.core.ui.SortMode

@Composable
fun WidgetConfigControls(
    widgetConfig: WidgetConfig,
    onStationButtonClicked: () -> Unit,
    onSortModeChanged: (SortMode) -> Unit,
    onOpacityChanged: (Float) -> Unit,
    onCancel: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.widget_settings),
            style = MaterialTheme.typography.titleMedium,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.station))
            FilledTonalButton(
                onClick = onStationButtonClicked,
                modifier = Modifier.fillMaxWidth(9 / 12f),
            ) {
                Text(text = widgetConfig.selectedStation?.name.orEmpty(), modifier = Modifier.basicMarquee())
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.sort_by), modifier = Modifier.weight(1f))
            SortMode.entries.forEach { mode ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        onSortModeChanged(mode)
                    },
                ) {
                    RadioButton(
                        selected = mode == widgetConfig.sortMode,
                        onClick = { onSortModeChanged(mode) },
                    )
                    Text(
                        stringResource(if (mode == SortMode.TIME) R.string.time else R.string.line),
                        modifier = Modifier.padding(end = 10.dp),
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.opacity))
            Slider(
                value = widgetConfig.opacity,
                onValueChange = onOpacityChanged,
                modifier = Modifier.fillMaxWidth(9 / 12f),
                steps = 9,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilledTonalButton(onClick = onCancel) {
                Text(text = stringResource(R.string.cancel))
            }
            Button(onClick = onDone) {
                Text(text = stringResource(R.string.done))
            }
        }
    }
}