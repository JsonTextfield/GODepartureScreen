package com.jsontextfield.departurescreen.data

import com.jsontextfield.departurescreen.ui.SortMode

interface IPreferencesRepository {
    suspend fun getHiddenTrains(): Set<String>?
    suspend fun setHiddenTrains(hiddenTrains: Set<String>)
    suspend fun getSortMode(): SortMode?
    suspend fun setSortMode(sortMode: SortMode)
}