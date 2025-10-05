package com.jsontextfield.departurescreen.ui

import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePreferencesRepository : IPreferencesRepository {
    private var visibleTrains: Set<String> = emptySet()
    private var favouriteStations: Set<String> = emptySet()
    private var selectedStationCode: String? = null
    private var sortMode: SortMode? = SortMode.TIME

    private var theme: ThemeMode? = ThemeMode.DEFAULT

    override fun getVisibleTrains(): Flow<Set<String>> {
        return flowOf(visibleTrains)
    }

    override suspend fun setVisibleTrains(visibleTrains: Set<String>) {
        this.visibleTrains = visibleTrains
    }

    override fun getSortMode(): Flow<SortMode> {
        return flowOf(sortMode ?: SortMode.TIME)
    }

    override suspend fun setSortMode(sortMode: SortMode) {
        this.sortMode = sortMode
    }

    override fun getSelectedStationCode(): Flow<String> {
        return flowOf(selectedStationCode ?: "UN")
    }

    override suspend fun setSelectedStationCode(stationCode: String) {
        this.selectedStationCode = stationCode
    }

    override fun getTheme(): Flow<ThemeMode> {
        return flowOf(theme ?: ThemeMode.DEFAULT)
    }

    override suspend fun setTheme(theme: ThemeMode) {
        this.theme = theme
    }

    override fun getFavouriteStations(): Flow<Set<String>> {
        return flowOf(favouriteStations)
    }

    override suspend fun setFavouriteStations(favouriteStations: Set<String>) {
        this.favouriteStations = favouriteStations
    }
}