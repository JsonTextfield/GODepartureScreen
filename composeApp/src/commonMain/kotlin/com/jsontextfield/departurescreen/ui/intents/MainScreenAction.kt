package com.jsontextfield.departurescreen.ui.intents

import com.jsontextfield.departurescreen.core.entities.Stop
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.SortMode

sealed interface MainScreenAction
sealed interface MainScreenNavigationAction : MainScreenAction
sealed interface ActionBarAction : MainScreenAction

data class SetVisibleTrains(val visibleTrains: Set<String>) : MainScreenAction
data object Refresh : MainScreenAction
data object Retry : MainScreenAction

data object Alerts : MainScreenNavigationAction, ActionBarAction
data object Settings : MainScreenNavigationAction, ActionBarAction
data class Stops(val selectedStopCode: String? = null) : MainScreenNavigationAction
data class TripDetails(val trip: Trip) : MainScreenNavigationAction

data class SetSortMode(val sortMode: SortMode) : ActionBarAction
data class SetFavouriteStop(val stop: Stop) : ActionBarAction

