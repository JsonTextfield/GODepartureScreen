package com.jsontextfield.departurescreen.ui

import com.jsontextfield.departurescreen.Train

data class UIState(
    val allTrains: List<Train> = emptyList(),
    val visibleTrains: Set<String> = emptySet(),
    val sortMode: SortMode = SortMode.TIME,
    val theme: ThemeMode = ThemeMode.DEFAULT,
)