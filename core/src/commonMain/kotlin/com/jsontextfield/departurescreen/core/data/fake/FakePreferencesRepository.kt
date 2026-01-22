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

    private val selectedStopCodeFlow = MutableStateFlow("UN")
    override fun getSelectedStopCode(): Flow<String> = selectedStopCodeFlow
    override suspend fun setSelectedStopCode(stopCode: String) {
        selectedStopCodeFlow.value = stopCode
    }

    private val themeFlow = MutableStateFlow(ThemeMode.DEFAULT)
    override fun getTheme(): Flow<ThemeMode> = themeFlow
    override suspend fun setTheme(theme: ThemeMode) {
        themeFlow.value = theme
    }

    private val favouriteStopsFlow = MutableStateFlow(emptySet<String>())
    override fun getFavouriteStops(): Flow<Set<String>> = favouriteStopsFlow
    override suspend fun setFavouriteStops(favouriteStops: Set<String>) {
        favouriteStopsFlow.value = favouriteStops
    }

    private val readAlertsFlow = MutableStateFlow(emptySet<String>())
    override fun getReadAlerts(): Flow<Set<String>> = readAlertsFlow

    override suspend fun addReadAlert(id: String) {
        readAlertsFlow.value += id
    }
}