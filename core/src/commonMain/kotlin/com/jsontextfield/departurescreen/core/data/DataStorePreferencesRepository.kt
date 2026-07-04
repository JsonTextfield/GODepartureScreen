package com.jsontextfield.departurescreen.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.jsontextfield.departurescreen.core.ui.ContrastMode
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorePreferencesRepository(
    private val dataStore: DataStore<Preferences>,
    private val onSetStop: (String) -> Unit = {},
    private val onSetSortMode: (SortMode) -> Unit = {},
    private val onSetVisibleTrains: (Set<String>) -> Unit = {},
    private val onSetFavouriteStops: (Set<String>) -> Unit = {},
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

    override fun getSelectedStop(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(SELECTED_STOP_KEY)] ?: preferences[stringPreferencesKey(OLD_SELECTED_STOP_CODE_KEY)] ?: "UN"
        }
    }

    override suspend fun setSelectedStop(stopName: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(SELECTED_STOP_KEY)] = stopName
        }
        onSetStop(stopName)
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

    override suspend fun setDynamicTheme(useDynamicTheme: Boolean) {
        dataStore.edit { preferences ->
            val key = booleanPreferencesKey(DYNAMIC_THEME_KEY)
            preferences[key] = useDynamicTheme
        }
    }

    override fun getDynamicTheme(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(DYNAMIC_THEME_KEY)] ?: false
        }
    }

    override suspend fun setContrast(contrast: ContrastMode) {
        dataStore.edit { preferences ->
            val key = intPreferencesKey(CONTRAST_KEY)
            preferences[key] = contrast.ordinal
        }
    }

    override fun getContrast(): Flow<ContrastMode> {
        return dataStore.data.map { preferences ->
            ContrastMode.entries.firstOrNull {
                it.ordinal == preferences[intPreferencesKey(CONTRAST_KEY)]
            } ?: ContrastMode.NORMAL
        }
    }

    override suspend fun setTimeFormat(timeFormat: TimeFormat) {
        dataStore.edit { preferences ->
            val key = intPreferencesKey(TIME_FORMAT_KEY)
            preferences[key] = timeFormat.ordinal
        }
    }

    override fun getTimeFormat(): Flow<TimeFormat> {
        return dataStore.data.map { preferences ->
            TimeFormat.entries.firstOrNull {
                it.ordinal == preferences[intPreferencesKey(TIME_FORMAT_KEY)]
            } ?: TimeFormat.RELATIVE
        }
    }

    override suspend fun setFavouriteStops(favouriteStops: Set<String>) {
        dataStore.edit { preferences ->
            preferences[stringSetPreferencesKey(FAVOURITE_STOPS_KEY)] = favouriteStops
        }
        onSetFavouriteStops(favouriteStops)
    }

    override fun getFavouriteStops(): Flow<Set<String>> {
        return dataStore.data.map { preferences ->
            preferences[stringSetPreferencesKey(FAVOURITE_STOPS_KEY)] ?: preferences[stringSetPreferencesKey(OLD_FAVOURITE_STOPS_KEY)] ?: emptySet()
        }
    }

    override fun getReadAlerts(): Flow<Set<String>> {
        return dataStore.data.map { preferences ->
            preferences[stringSetPreferencesKey(READ_ALERTS_KEY)] ?: emptySet()
        }
    }

    override suspend fun addReadAlert(id: String) {
        dataStore.edit { preferences ->
            val readAlerts = preferences[stringSetPreferencesKey(READ_ALERTS_KEY)] ?: emptySet()
            preferences[stringSetPreferencesKey(READ_ALERTS_KEY)] = readAlerts + id
        }
    }

    override fun getUseAlertsWithLinks(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(USE_ALERTS_WITH_LINKS_KEY)] ?: false
        }
    }

    override suspend fun setUseAlertsWithLinks(useAlertsWithLinks: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(USE_ALERTS_WITH_LINKS_KEY)] = useAlertsWithLinks
        }
    }

    override fun getVisibleAlertLines(): Flow<Set<String>> {
        return dataStore.data.map { preferences ->
            preferences[stringSetPreferencesKey(VISIBLE_ALERT_LINES_KEY)] ?: emptySet()
        }
    }

    override suspend fun setVisibleAlertLines(lines: Set<String>) {
        dataStore.edit { preferences ->
            preferences[stringSetPreferencesKey(VISIBLE_ALERT_LINES_KEY)] = lines
        }
    }

    override fun getIsUnreadAlertsSelected(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(IS_UNREAD_ALERTS_SELECTED_KEY)] ?: false
        }
    }

    override suspend fun setIsUnreadAlertsSelected(isSelected: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(IS_UNREAD_ALERTS_SELECTED_KEY)] = isSelected
        }
    }
}
