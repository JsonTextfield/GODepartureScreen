package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.ui.menu.Action
import com.jsontextfield.departurescreen.ui.menu.ActionBar
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    allTrains: List<Train>,
    visibleTrains: Set<String>,
    timeRemaining: Int,
    actions: List<Action>,
    onSetVisibleTrains: (Set<String>) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.app_name),
                        modifier = Modifier
                            .semantics { heading() }
                            .basicMarquee(),
                    )
                },
                actions = {
                    val screenWidthDp =
                        (LocalWindowInfo.current.containerSize.width / LocalDensity.current.density).toInt()
                    val maxActions = when {
                        screenWidthDp < 400 -> screenWidthDp / 4 / 48
                        screenWidthDp < 600 -> screenWidthDp / 3 / 48
                        screenWidthDp < 800 -> screenWidthDp / 2 / 48
                        else -> screenWidthDp * 2 / 3 / 48
                    } + 1
                    ActionBar(
                        maxActions = maxActions,
                        actions = actions,
                    )
                },
                modifier = Modifier.shadow(4.dp)
            )
        },
        floatingActionButton = {
            CountdownTimer(timeRemaining)
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
            FilterChipStrip(
                data = allTrains.distinctBy { it.code to it.name }.sortedBy { it.code },
                selectedItems = visibleTrains,
                onSelectionChanged = onSetVisibleTrains
            )
            Surface {
                TrainList(trains = allTrains.filter { it.isVisible })
            }
        }
    }
}