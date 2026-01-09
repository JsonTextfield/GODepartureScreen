package com.jsontextfield.departurescreen.widget.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jsontextfield.departurescreen.core.ui.SortMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Define a DataStore instance at the top level. The name is for the file where settings are stored.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_settings")

class WidgetConfigDataStore(private val context: Context) {

    // Define preference keys. We will append the GlanceId to make them unique.
    private object PreferenceKeys {
        fun selectedStation(appWidgetId: Int) = stringPreferencesKey("selected_station_${appWidgetId}")
        fun sortMode(appWidgetId: Int) = intPreferencesKey("sort_mode_${appWidgetId}")
        fun opacity(appWidgetId: Int) = floatPreferencesKey("opacity_${appWidgetId}")
    }

    // Read the config for a specific widget as a Flow
    fun getConfig(appWidgetId: Int): Flow<WidgetConfig> = context.dataStore.data
        .map { preferences ->
            WidgetConfig(
                selectedStationCode = preferences[PreferenceKeys.selectedStation(appWidgetId)] ?: "UN",
                sortMode = SortMode.entries.firstOrNull {
                    preferences[PreferenceKeys.sortMode(appWidgetId)] == it.ordinal
                } ?: SortMode.TIME,
                opacity = preferences[PreferenceKeys.opacity(appWidgetId)] ?: 0.8f,
            )
        }

    // Save the config for a specific widget
    suspend fun saveConfig(appWidgetId: Int, config: WidgetConfig) {
        context.dataStore.edit { preferences ->
            config.selectedStationCode?.let {
                preferences[PreferenceKeys.selectedStation(appWidgetId)] = it
            }
            preferences[PreferenceKeys.sortMode(appWidgetId)] = config.sortMode.ordinal
            preferences[PreferenceKeys.opacity(appWidgetId)] = config.opacity
        }
    }
}
