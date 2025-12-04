package com.jsontextfield.departurescreen.core.data.fake

import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePreferencesRepository : IPreferencesRepository {

    private val hiddenTrainsFlow = MutableStateFlow(emptySet<String>())
    override fun getVisibleTrains(): Flow<Set<String>> = hiddenTrainsFlow
    override suspend fun setVisibleTrains(visibleTrains: Set<String>) {
        hiddenTrainsFlow.value = visibleTrains
    }

    private val sortModeFlow = MutableStateFlow(SortMode.TIME)
    override fun getSortMode(): Flow<SortMode> = sortModeFlow
    override suspend fun setSortMode(sortMode: SortMode) {
        sortModeFlow.value = sortMode
    }

    private val selectedStationCodeFlow = MutableStateFlow("UN")
    override fun getSelectedStationCode(): Flow<String> = selectedStationCodeFlow
    override suspend fun setSelectedStationCode(stationCode: String) {
        selectedStationCodeFlow.value = stationCode
    }

    private val themeFlow = MutableStateFlow(ThemeMode.DEFAULT)
    override fun getTheme(): Flow<ThemeMode> = themeFlow
    override suspend fun setTheme(theme: ThemeMode) {
        themeFlow.value = theme
    }

    private val favouriteStationsFlow = MutableStateFlow(emptySet<String>())
    override fun getFavouriteStations(): Flow<Set<String>> = favouriteStationsFlow
    override suspend fun setFavouriteStations(favouriteStations: Set<String>) {
        favouriteStationsFlow.value = favouriteStations
    }
}