package com.jsontextfield.departurescreen.data

import com.jsontextfield.departurescreen.ui.SortMode

interface IPreferencesRepository {
    suspend fun getVisibleTrains(): Set<String>?
    suspend fun setVisibleTrains(visibleTrains: Set<String>)
    suspend fun getSortMode(): SortMode?
    suspend fun setSortMode(sortMode: SortMode)
}