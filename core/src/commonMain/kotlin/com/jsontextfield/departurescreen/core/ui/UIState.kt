package com.jsontextfield.departurescreen.core.ui

import com.jsontextfield.departurescreen.core.entities.CombinedStation
import com.jsontextfield.departurescreen.core.entities.Trip

data class UIState(
    val status: Status = Status.LOADING,
    val selectedStation: CombinedStation? = null,
    private val _allTrips: List<Trip> = emptyList(),
    val allStations: List<CombinedStation> = emptyList(),
    val visibleTrains: Set<String> = emptySet(),
    val favouriteStations: Set<String> = emptySet(),
    val sortMode: SortMode = SortMode.TIME,
    val theme: ThemeMode = ThemeMode.DEFAULT,
    val isRefreshing: Boolean = false,
    val isAlertsRefreshing: Boolean = false,
) {
    val allTrips: List<Trip>
        get() {
            return _allTrips.map { train ->
                train.copy(isVisible = train.code in visibleTrains || visibleTrains.isEmpty())
            }.sortedWith(
                when (sortMode) {
                    SortMode.TIME -> compareBy { it.departureTime }
                    SortMode.LINE -> compareBy({ it.code }, { it.destination })
                }
            )
        }

    fun getFilteredStations(query: String): List<CombinedStation> {
        if (query.isBlank()) return allStations
        val lowerCaseQuery = query.lowercase()
        return allStations.filter {
            lowerCaseQuery in it.name.lowercase()
        }
    }
}


enum class Status {
    LOADING,
    LOADED,
    ERROR,
}