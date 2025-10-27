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
    private val dataStore: DataStore<Preferences>,
    private val onSetStation: (String) -> Unit = {},
    private val onSetSortMode: (SortMode) -> Unit = {},
    private val onSetVisibleTrains: (Set<String>) -> Unit = {},
) : IPreferencesRepository {
    override fun getVisibleTrains(): Flow<Set<String>> {
        return dataStore.data.map { preferences ->
            preferences[stringSetPreferencesKey(HIDDEN_TRAINS_KEY)] ?: emptySet()
        }
    }

    override suspend fun setVisibleTrains(visibleTrains: Set<String>) {
        dataStore.edit { preferences ->
            preferences[stringSetPreferencesKey(HIDDEN_TRAINS_KEY)] = visibleTrains
        }
        onSetVisibleTrains(visibleTrains)
    }

    override fun getSortMode(): Flow<SortMode> {
        return dataStore.data.map { preferences ->
            SortMode.entries.firstOrNull {
                it.ordinal == preferences[intPreferencesKey(SORT_MODE_KEY)]
            } ?: SortMode.TIME
        }
    }

    override suspend fun setSortMode(sortMode: SortMode) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey(SORT_MODE_KEY)] = sortMode.ordinal
        }
        onSetSortMode(sortMode)
    }

    override fun getSelectedStationCode(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(SELECTED_STATION_CODE_KEY)] ?: "UN"
        }
    }

    override suspend fun setSelectedStationCode(stationCode: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(SELECTED_STATION_CODE_KEY)] = stationCode
        }
        onSetStation(stationCode)
    }

    override suspend fun setTheme(theme: ThemeMode) {
        dataStore.edit { preferences ->
            val key = intPreferencesKey(THEME_KEY)
            preferences[key] = theme.ordinal
        }
    }

    override fun getTheme(): Flow<ThemeMode> {
        return dataStore.data.map { preferences ->
            ThemeMode.entries.firstOrNull {
                it.ordinal == preferences[intPreferencesKey(THEME_KEY)]
            } ?: ThemeMode.DEFAULT
        }
    }

    override suspend fun setFavouriteStations(favouriteStations: Set<String>) {
        dataStore.edit { preferences ->
            preferences[stringSetPreferencesKey(FAVOURITE_STATIONS_KEY)] = favouriteStations
        }
    }

    override fun getFavouriteStations(): Flow<Set<String>> {
        return dataStore.data.map { preferences ->
            preferences[stringSetPreferencesKey(FAVOURITE_STATIONS_KEY)] ?: emptySet()
        }
    }

    private companion object {
        const val SORT_MODE_KEY = "sortMode"
        const val THEME_KEY = "theme"
        const val SELECTED_STATION_CODE_KEY = "selectedStationCode"
        const val FAVOURITE_STATIONS_KEY = "favouriteStations"
        const val HIDDEN_TRAINS_KEY = "hiddenTrains"
    }
}