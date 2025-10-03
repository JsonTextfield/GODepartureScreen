package com.jsontextfield.departurescreen.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorePreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : IPreferencesRepository {
    override fun getVisibleTrains(): Flow<Set<String>> {
        return dataStore.data.map { preferences ->
            preferences[stringSetPreferencesKey("hiddenTrains")] ?: emptySet()
        }
    }

    override suspend fun setVisibleTrains(visibleTrains: Set<String>) {
        dataStore.edit { preferences ->
            preferences[stringSetPreferencesKey("hiddenTrains")] = visibleTrains
        }
    }

    override fun getSortMode(): Flow<SortMode> {
        return dataStore.data.map { preferences ->
            SortMode.entries[preferences[intPreferencesKey("sortMode")] ?: 0]
        }
    }

    override suspend fun setSortMode(sortMode: SortMode) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey("sortMode")] = sortMode.ordinal
        }
    }

    override fun getSelectedStationCode(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey("selectedStationCode")] ?: "UN"
        }
    }

    override suspend fun setSelectedStationCode(stationCode: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("selectedStationCode")] = stationCode
        }
    }

    override suspend fun setTheme(theme: ThemeMode) {
        dataStore.edit { preferences ->
            val key = intPreferencesKey("theme")
            preferences[key] = theme.ordinal
        }
    }

    override fun getTheme(): Flow<ThemeMode> {
        return dataStore.data.map { preferences ->
            ThemeMode.entries[preferences[intPreferencesKey("theme")] ?: 0]
        }
    }

    override suspend fun setFavouriteStations(favouriteStations: Set<String>) {
        dataStore.edit { preferences ->
            preferences[stringSetPreferencesKey("favouriteStations")] = favouriteStations
        }
    }

    override fun getFavouriteStations(): Flow<Set<String>> {
        return dataStore.data.map { preferences ->
            preferences[stringSetPreferencesKey("favouriteStations")] ?: emptySet()
        }
    }
}