package com.jsontextfield.departurescreen.ui

import com.jsontextfield.departurescreen.entities.CombinedStation
import com.jsontextfield.departurescreen.entities.Station
import com.jsontextfield.departurescreen.entities.Train

data class UIState(
    val status: Status = Status.LOADING,
    val selectedStation: CombinedStation? = null,
    private val _allTrains: List<Train> = emptyList(),
    val allStations: List<CombinedStation> = emptyList(),
    val visibleTrains: Set<String> = emptySet(),
    val favouriteStations: Set<String> = emptySet(),
    val sortMode: SortMode = SortMode.TIME,
    val theme: ThemeMode = ThemeMode.DEFAULT,
    val isRefreshing: Boolean = false,
) {
    val allTrains: List<Train>
        get() {
            return _allTrains.map { train ->
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