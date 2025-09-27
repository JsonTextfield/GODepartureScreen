package com.jsontextfield.departurescreen.data

import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode

interface IPreferencesRepository {
    suspend fun getVisibleTrains(): Set<String>?
    suspend fun setVisibleTrains(visibleTrains: Set<String>)
    suspend fun getSortMode(): SortMode?
    suspend fun setSortMode(sortMode: SortMode)
    suspend fun getSelectedStationCode(): String?
    suspend fun setSelectedStationCode(stationCode: String)
    suspend fun getTheme(): ThemeMode?
    suspend fun setTheme(theme: ThemeMode)
    suspend fun getFavouriteStations(): Set<String>?
    suspend fun setFavouriteStations(favouriteStations: Set<String>)
}