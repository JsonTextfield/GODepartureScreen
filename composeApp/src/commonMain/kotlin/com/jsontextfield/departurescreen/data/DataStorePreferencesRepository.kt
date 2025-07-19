package com.jsontextfield.departurescreen.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.jsontextfield.departurescreen.ui.SortMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStorePreferencesRepository(
    private val dataStore: DataStore<Preferences>?
) : IPreferencesRepository {
    override suspend fun getVisibleTrains(): Set<String>? {
        return dataStore?.data?.map { preferences ->
            preferences[stringSetPreferencesKey("hiddenTrains")] ?: emptySet()
        }?.first()
    }

    override suspend fun setVisibleTrains(visibleTrains: Set<String>) {
        dataStore?.edit { preferences ->
            preferences[stringSetPreferencesKey("hiddenTrains")] = visibleTrains
        }
    }

    override suspend fun getSortMode(): SortMode? {
        return dataStore?.data?.map { preferences ->
            val sortModeOrdinal = preferences[intPreferencesKey("sortMode")] ?: SortMode.TIME
            SortMode.entries.firstOrNull { sortModeOrdinal == it.ordinal } ?: SortMode.TIME
        }?.first()
    }

    override suspend fun setSortMode(sortMode: SortMode) {
        dataStore?.edit { preferences ->
            preferences[intPreferencesKey("sortMode")] = sortMode.ordinal
        }
    }
}