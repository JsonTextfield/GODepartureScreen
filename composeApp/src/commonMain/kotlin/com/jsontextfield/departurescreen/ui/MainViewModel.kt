package com.jsontextfield.departurescreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.data.IGoTrainDataSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val goTrainDataSource: IGoTrainDataSource) : ViewModel() {

    private var _trains: MutableStateFlow<List<Train>> = MutableStateFlow(emptyList())
    val trains: StateFlow<List<Train>> = _trains.asStateFlow()

    private var _displayedTrains: MutableStateFlow<List<Train>> = MutableStateFlow(emptyList())
    val displayedTrains: StateFlow<List<Train>> = _displayedTrains.asStateFlow()

    private var _timeRemaining: MutableStateFlow<Int> = MutableStateFlow(0)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private var _hiddenTrains: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    val hiddenTrains: StateFlow<Set<String>> = _hiddenTrains.asStateFlow()

    private var _sortMode: MutableStateFlow<SortMode> = MutableStateFlow(SortMode.TIME)
    val sortMode: StateFlow<SortMode> = _sortMode.asStateFlow()

    private var timerJob: Job? = null

    init {
        timerJob = timerJob ?: viewModelScope.launch {
            while (true) {
                if (timeRemaining.value <= 0) {
                    viewModelScope.launch {
                        _trains.value = goTrainDataSource.getTrains()
                        updateDisplayedTrains()
                    }
                    _timeRemaining.value = 20_000
                } else {
                    delay(1000)
                    _timeRemaining.value -= 1000
                }
            }
        }
    }

    fun setSortMode(mode: SortMode) {
        _sortMode.value = mode
        updateDisplayedTrains()
    }

    fun setHiddenTrains(hiddenTrains: Set<String>) {
        _hiddenTrains.value = hiddenTrains
        updateDisplayedTrains()
    }

    private fun updateDisplayedTrains() {
        _displayedTrains.value = _trains.value
            .filter { train -> train.code !in _hiddenTrains.value }
            .sortedWith(
                when (sortMode.value) {
                    SortMode.TIME -> compareBy { it.departureTime }
                    SortMode.CODE -> compareBy({ it.code }, { it.name })
                }
            )
    }
}

enum class SortMode { TIME, CODE }