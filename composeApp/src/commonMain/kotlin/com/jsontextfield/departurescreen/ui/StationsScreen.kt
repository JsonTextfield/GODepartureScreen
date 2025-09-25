@file:OptIn(ExperimentalMaterial3Api::class)

package com.jsontextfield.departurescreen.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.entities.CombinedStation
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.back
import departure_screen.composeapp.generated.resources.clear
import departure_screen.composeapp.generated.resources.scroll_to_top
import departure_screen.composeapp.generated.resources.search
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.E

@Composable
fun StationsScreen(uiState: UIState, onStationSelected: (CombinedStation) -> Unit, onBackPressed: () -> Unit) {
    val textFieldState = rememberTextFieldState()
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    Back(onBackPressed)
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                        )
                    }
                },
                title = {
                    BasicTextField(
                        textFieldState,
                        modifier = Modifier
                            .widthIn(max = 300.dp)
                            .padding(vertical = 12.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(24.dp)
                            ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search,
                            showKeyboardOnFocus = false
                        ),
                        decorator = { innerTextField ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .heightIn(min = 56.dp)
                            ) {
                                Icon(Icons.Rounded.Search, contentDescription = null)
                                Box(modifier = Modifier.weight(1f)) {
                                    if (textFieldState.text.isEmpty()) {
                                        Text(
                                            stringResource(Res.string.search),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.onBackground.copy(
                                                    alpha = .8f
                                                )
                                            )
                                        )
                                    }
                                    innerTextField()
                                }
                                if (textFieldState.text.isNotEmpty()) {
                                    IconButton(onClick = {
                                        textFieldState.setTextAndPlaceCursorAtEnd("")
                                    }) {
                                        Icon(
                                            Icons.Rounded.Clear,
                                            contentDescription = stringResource(Res.string.clear),
                                        )
                                    }
                                }
                            }
                        },
                    )
                },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = gridState.firstVisibleItemIndex > 10,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 },
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            gridState.animateScrollToItem(0)
                        }
                    },
                    shape = SquircleShape(E)
                ) {
                    Icon(
                        Icons.Rounded.ArrowUpward,
                        contentDescription = stringResource(Res.string.scroll_to_top),
                    )
                }
            }
        },
        modifier = Modifier.shadow(4.dp)
    ) {
        val filteredStations = uiState.getFilteredStations(textFieldState.text.toString())
        LazyVerticalGrid(
            state = gridState,
            modifier = Modifier.padding(it).semantics {
                collectionInfo = CollectionInfo(
                    rowCount = filteredStations.size,
                    columnCount = 1,
                )
            },
            columns = GridCells.Adaptive(300.dp)
        ) {
            itemsIndexed(
                filteredStations,
                key = { _, station -> station.codes }) { index, station ->
                StationListItem(
                    station,
                    isSelected = station.codes.any { it in uiState.selectedStation?.codes.orEmpty() },
                    modifier = Modifier.clickable {
                        onStationSelected(station)
                        onBackPressed()
                    }.semantics {
                        CollectionItemInfo(
                            rowIndex = index,
                            rowSpan = 1,
                            columnIndex = 0,
                            columnSpan = 1,
                        )
                    }.animateItem()
                )
            }
        }
    }
}
