package com.jsontextfield.departurescreen.core.data

import com.jsontextfield.departurescreen.core.ui.ContrastMode
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import com.jsontextfield.departurescreen.core.ui.TimeFormat
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
    fun getDynamicTheme(): Flow<Boolean>
    suspend fun setDynamicTheme(useDynamicTheme: Boolean)
    fun getContrast(): Flow<ContrastMode>
    suspend fun setContrast(contrast: ContrastMode)
    fun getTimeFormat(): Flow<TimeFormat>
    suspend fun setTimeFormat(timeFormat: TimeFormat)
    fun getFavouriteStops(): Flow<Set<String>>
    suspend fun setFavouriteStops(favouriteStops: Set<String>)

    fun getReadAlerts() : Flow<Set<String>>
    suspend fun addReadAlert(id: String)
}

const val SORT_MODE_KEY = "sortMode"
const val THEME_KEY = "theme"
const val DYNAMIC_THEME_KEY = "dynamicTheme"
const val CONTRAST_KEY = "contrast"
const val TIME_FORMAT_KEY = "timeFormat"
const val OLD_SELECTED_STOP_CODE_KEY = "selectedStationCode"
const val SELECTED_STOP_CODE_KEY = "selectedStopCode"
const val OLD_FAVOURITE_STOPS_KEY = "favouriteStations"
const val FAVOURITE_STOPS_KEY = "favouriteStops"
const val HIDDEN_TRAINS_KEY = "hiddenTrains"
const val READ_ALERTS_KEY = "readAlerts"
