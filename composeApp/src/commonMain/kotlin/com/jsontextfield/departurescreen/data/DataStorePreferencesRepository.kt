package com.jsontextfield.departurescreen.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.jsontextfield.departurescreen.ui.SortMode
import com.jsontextfield.departurescreen.ui.ThemeMode
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
            SortMode.entries.firstOrNull { preferences[intPreferencesKey("sortMode")] == it.ordinal }
        }?.first()
    }

    override suspend fun setSortMode(sortMode: SortMode) {
        dataStore?.edit { preferences ->
            preferences[intPreferencesKey("sortMode")] = sortMode.ordinal
        }
    }

    override suspend fun setTheme(theme: ThemeMode) {
        dataStore?.edit { preferences ->
            val key = intPreferencesKey("theme")
            preferences[key] = theme.ordinal
        }
    }

    override suspend fun getTheme(): ThemeMode? {
        return dataStore?.data?.map { preferences ->
            ThemeMode.entries.firstOrNull { preferences[intPreferencesKey("theme")] == it.ordinal }
        }?.first()
    }
}