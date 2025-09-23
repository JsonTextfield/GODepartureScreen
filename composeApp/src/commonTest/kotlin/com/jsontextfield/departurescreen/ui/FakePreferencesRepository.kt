package com.jsontextfield.departurescreen.ui

import com.jsontextfield.departurescreen.data.IPreferencesRepository

class FakePreferencesRepository : IPreferencesRepository {
    private var visibleTrains: Set<String> = emptySet()
    private var favouriteStations: Set<String> = emptySet()
    private var selectedStationCode: String? = null
    private var sortMode: SortMode? = SortMode.TIME

    private var theme: ThemeMode? = ThemeMode.DEFAULT

    override suspend fun getVisibleTrains(): Set<String>? {
        return visibleTrains
    }

    override suspend fun setVisibleTrains(visibleTrains: Set<String>) {
        this.visibleTrains = visibleTrains
    }

    override suspend fun getSortMode(): SortMode? {
        return sortMode
    }

    override suspend fun setSortMode(sortMode: SortMode) {
        this.sortMode = sortMode
    }

    override suspend fun getSelectedStationCode(): String? {
        return selectedStationCode
    }

    override suspend fun setSelectedStationCode(stationCode: String) {
        this.selectedStationCode = stationCode
    }

    override suspend fun getTheme(): ThemeMode? {
        return theme
    }

    override suspend fun setTheme(theme: ThemeMode) {
        this.theme = theme
    }

    override suspend fun getFavouriteStations(): Set<String>? {
        return favouriteStations
    }

    override suspend fun setFavouriteStations(favouriteStations: Set<String>) {
        this.favouriteStations = favouriteStations
    }
}