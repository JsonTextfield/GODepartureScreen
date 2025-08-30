package com.jsontextfield.departurescreen.ui

import com.jsontextfield.departurescreen.entities.Train
import com.jsontextfield.departurescreen.entities.Station
import kotlin.collections.sortedWith
import kotlin.comparisons.compareBy

data class UIState(
    val status: Status = Status.LOADING,
    val selectedStation: Station? = null,
    private val _allTrains: List<Train> = emptyList(),
    val allStations: List<Station> = emptyList(),
    val visibleTrains: Set<String> = emptySet(),
    val sortMode: SortMode = SortMode.TIME,
    val theme: ThemeMode = ThemeMode.DEFAULT,
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
}


enum class Status {
    LOADING,
    LOADED,
    ERROR,
}