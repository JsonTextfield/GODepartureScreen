package com.jsontextfield.departurescreen.ui

import com.jsontextfield.departurescreen.data.IPreferencesRepository

class FakePreferencesRepository : IPreferencesRepository {
    private var visibleTrains: Set<String> = emptySet()
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

    override suspend fun getTheme(): ThemeMode? {
        return theme
    }

    override suspend fun setTheme(theme: ThemeMode) {
        this.theme = theme
    }
}