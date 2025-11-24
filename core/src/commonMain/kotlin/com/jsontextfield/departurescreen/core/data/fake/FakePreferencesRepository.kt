package com.jsontextfield.departurescreen.core.data.fake

import com.jsontextfield.departurescreen.core.data.FAVOURITE_STATIONS_KEY
import com.jsontextfield.departurescreen.core.data.HIDDEN_TRAINS_KEY
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.SELECTED_STATION_CODE_KEY
import com.jsontextfield.departurescreen.core.data.SORT_MODE_KEY
import com.jsontextfield.departurescreen.core.data.THEME_KEY
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePreferencesRepository : IPreferencesRepository {

    var data = mutableMapOf<String, Any>()

    override fun getVisibleTrains(): Flow<Set<String>> {
        return flow {
            emit(data[HIDDEN_TRAINS_KEY] as? Set<String> ?: emptySet())
        }
    }

    override suspend fun setVisibleTrains(visibleTrains: Set<String>) {
        data[HIDDEN_TRAINS_KEY] = visibleTrains
    }

    override fun getSortMode(): Flow<SortMode> {
        return flow {
            emit(data[SORT_MODE_KEY] as? SortMode ?: SortMode.TIME)
        }
    }

    override suspend fun setSortMode(sortMode: SortMode) {
        data[SORT_MODE_KEY] = sortMode
    }

    override fun getSelectedStationCode(): Flow<String> {
        return flow {
            emit(data[SELECTED_STATION_CODE_KEY] as? String ?: "UN")
        }
    }

    override suspend fun setSelectedStationCode(stationCode: String) {
        data[SELECTED_STATION_CODE_KEY] = stationCode
    }

    override fun getTheme(): Flow<ThemeMode> {
        return flow {
            emit(data[THEME_KEY] as? ThemeMode ?: ThemeMode.DEFAULT)
        }
    }

    override suspend fun setTheme(theme: ThemeMode) {
        data[THEME_KEY] = theme
    }

    override fun getFavouriteStations(): Flow<Set<String>> {
        return flow {
            emit(data[FAVOURITE_STATIONS_KEY] as? Set<String> ?: emptySet())
        }
    }

    override suspend fun setFavouriteStations(favouriteStations: Set<String>) {
        data[FAVOURITE_STATIONS_KEY] = favouriteStations
    }
}