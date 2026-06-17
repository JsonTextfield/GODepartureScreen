package com.jsontextfield.departurescreen.core.data.fake

import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.ui.ContrastMode
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePreferencesRepository : IPreferencesRepository {

    private val hiddenTrainsFlow = MutableStateFlow(emptySet<String>())
    override fun getVisibleTrains(): Flow<Set<String>> = hiddenTrainsFlow
    override suspend fun setVisibleTrains(visibleTrains: Set<String>) {
        hiddenTrainsFlow.value = visibleTrains
    }

    private val sortModeFlow = MutableStateFlow(SortMode.TIME)
    override fun getSortMode(): Flow<SortMode> = sortModeFlow
    override suspend fun setSortMode(sortMode: SortMode) {
        sortModeFlow.value = sortMode
    }

    private val selectedStopCodeFlow = MutableStateFlow("UN")
    override fun getSelectedStop(): Flow<String> = selectedStopCodeFlow
    override suspend fun setSelectedStop(stopName: String) {
        selectedStopCodeFlow.value = stopName
    }

    private val themeFlow = MutableStateFlow(ThemeMode.DEFAULT)
    override fun getTheme(): Flow<ThemeMode> = themeFlow
    override suspend fun setTheme(theme: ThemeMode) {
        themeFlow.value = theme
    }

    private val dynamicThemeFlow = MutableStateFlow(false)
    override fun getDynamicTheme(): Flow<Boolean> = dynamicThemeFlow
    override suspend fun setDynamicTheme(useDynamicTheme: Boolean) {
        dynamicThemeFlow.value = useDynamicTheme
    }

    private val contrastFlow = MutableStateFlow(ContrastMode.NORMAL)
    override fun getContrast(): Flow<ContrastMode> = contrastFlow
    override suspend fun setContrast(contrast: ContrastMode) {
        contrastFlow.value = contrast
    }

    private val timeFormatFlow = MutableStateFlow(TimeFormat.RELATIVE)
    override fun getTimeFormat(): Flow<TimeFormat> = timeFormatFlow
    override suspend fun setTimeFormat(timeFormat: TimeFormat) {
        timeFormatFlow.value = timeFormat
    }

    private val favouriteStopsFlow = MutableStateFlow(emptySet<String>())
    override fun getFavouriteStops(): Flow<Set<String>> = favouriteStopsFlow
    override suspend fun setFavouriteStops(favouriteStops: Set<String>) {
        favouriteStopsFlow.value = favouriteStops
    }

    private val readAlertsFlow = MutableStateFlow(emptySet<String>())
    override fun getReadAlerts(): Flow<Set<String>> = readAlertsFlow

    override suspend fun addReadAlert(id: String) {
        readAlertsFlow.value += id
    }

    private val useAlertsWithLinksFlow = MutableStateFlow(false)
    override fun getUseAlertsWithLinks(): Flow<Boolean> = useAlertsWithLinksFlow
    override suspend fun setUseAlertsWithLinks(useAlertsWithLinks: Boolean) {
        useAlertsWithLinksFlow.value = useAlertsWithLinks
    }
}
