package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jsontextfield.departurescreen.Train
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.cancel
import departure_screen.composeapp.generated.resources.filter
import org.jetbrains.compose.resources.stringResource

@Composable
fun FilterTrainDialog(
    data: List<Train>,
    selectedItems: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var selection: Set<String> by remember { mutableStateOf(selectedItems) }
    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column {
                LazyVerticalGrid(
                    GridCells.Fixed(3),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(data) { train ->
                        TrainCodeBox(
                            train.code,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = train.color.copy(alpha = if (train.code in selection) 0.5f else 1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clickable {
                                    selection = if (train.code in selection) {
                                        selection - train.code
                                    } else {
                                        selection + train.code
                                    }
                                }
                                .semantics {
                                    contentDescription = train.name
                                },
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismissRequest,
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            onSelectionChanged(selection)
                            onDismissRequest()
                        },
                    ) {
                        Text(stringResource(Res.string.filter))
                    }
                }
            }

        }
    }
}