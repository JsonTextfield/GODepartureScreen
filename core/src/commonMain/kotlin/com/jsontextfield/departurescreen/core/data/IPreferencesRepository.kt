package com.jsontextfield.departurescreen.core.data

import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import kotlinx.coroutines.flow.Flow

interface IPreferencesRepository {
    fun getVisibleTrains(): Flow<Set<String>>
    suspend fun setVisibleTrains(visibleTrains: Set<String>)
    fun getSortMode(): Flow<SortMode>
    suspend fun setSortMode(sortMode: SortMode)
    fun getSelectedStopCode(): Flow<String>
    suspend fun setSelectedStopCode(stopCode: String)
    fun getTheme(): Flow<ThemeMode>
    suspend fun setTheme(theme: ThemeMode)
    fun getFavouriteStops(): Flow<Set<String>>
    suspend fun setFavouriteStops(favouriteStops: Set<String>)

    fun getReadAlerts() : Flow<Set<String>>
    suspend fun addReadAlert(id: String)
}

const val SORT_MODE_KEY = "sortMode"
const val THEME_KEY = "theme"
const val OLD_SELECTED_STOP_CODE_KEY = "selectedStationCode"
const val SELECTED_STOP_CODE_KEY = "selectedStopCode"
const val OLD_FAVOURITE_STOPS_KEY = "favouriteStations"
const val FAVOURITE_STOPS_KEY = "favouriteStops"
const val HIDDEN_TRAINS_KEY = "hiddenTrains"
const val READ_ALERTS_KEY = "readAlerts"