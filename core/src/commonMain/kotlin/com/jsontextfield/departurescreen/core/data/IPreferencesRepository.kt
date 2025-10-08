package com.jsontextfield.departurescreen.core.data

import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import kotlinx.coroutines.flow.Flow

interface IPreferencesRepository {
    fun getVisibleTrains(): Flow<Set<String>>
    suspend fun setVisibleTrains(visibleTrains: Set<String>)
    fun getSortMode(): Flow<SortMode>
    suspend fun setSortMode(sortMode: SortMode)
    fun getSelectedStationCode(): Flow<String>
    suspend fun setSelectedStationCode(stationCode: String)
    fun getTheme(): Flow<ThemeMode>
    suspend fun setTheme(theme: ThemeMode)
    fun getFavouriteStations(): Flow<Set<String>>
    suspend fun setFavouriteStations(favouriteStations: Set<String>)
}