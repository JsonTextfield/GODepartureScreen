package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.PopupProperties

@Composable
fun SortTrainMenu(
    isExpanded: Boolean,
    sortMode: SortMode,
    onSortModeChanged: (SortMode) -> Unit,
    onDismissRequest: () -> Unit,
) {
    DropdownMenu(properties = PopupProperties(),expanded = isExpanded, onDismissRequest = onDismissRequest) {
        SortMode.entries.forEach { mode ->
            DropdownMenuItem(text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(sortMode == mode, onClick = {
                        onSortModeChanged(mode)
                        onDismissRequest()
                    })
                    Text(
                        text = when (mode) {
                            SortMode.TIME -> "Time"
                            SortMode.CODE -> "Line"
                        },
                    )
                }
            }, onClick = {
                onSortModeChanged(mode)
                onDismissRequest()
            })
        }
    }
}