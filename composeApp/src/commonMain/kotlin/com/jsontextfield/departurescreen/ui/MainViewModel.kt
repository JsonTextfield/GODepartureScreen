package com.jsontextfield.departurescreen.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.data.IGoTrainDataSource
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.line
import departure_screen.composeapp.generated.resources.time
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import org.jetbrains.compose.resources.StringResource

class MainViewModel(private val goTrainDataSource: IGoTrainDataSource) : ViewModel() {

    private var _allTrains: MutableStateFlow<List<Train>> = MutableStateFlow(emptyList())
    val allTrains: StateFlow<List<Train>> = _allTrains.asStateFlow()

    private var _hiddenTrains: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    val hiddenTrains: StateFlow<Set<String>> = _hiddenTrains.asStateFlow()

    private var _timeRemaining: MutableStateFlow<Int> = MutableStateFlow(0)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private var _sortMode: MutableStateFlow<SortMode> = MutableStateFlow(SortMode.TIME)
    val sortMode: StateFlow<SortMode> = _sortMode.asStateFlow()

    var showFilterDialog by mutableStateOf(false)

    private var timerJob: Job? = null

    init {
        timerJob = timerJob ?: viewModelScope.launch {
            while (true) {
                if (timeRemaining.value <= 0) {
                    try {
                        _allTrains.value = goTrainDataSource.getTrains()
                        setHiddenTrains(_hiddenTrains.value)
                        setSortMode(_sortMode.value)
                        _timeRemaining.value = 20_000
                    } catch (exception: IOException) {
                        _timeRemaining.value = 1000
                    }
                }
                else {
                    delay(1000)
                    _timeRemaining.value -= 1000
                }
            }
        }
    }

    fun setSortMode(mode: SortMode) {
        _sortMode.value = mode
        _allTrains.value = _allTrains.value.sortedWith(
            when (mode) {
                SortMode.TIME -> compareBy { it.departureTime }
                SortMode.LINE -> compareBy({ it.code }, { it.destination })
            }
        )
    }

    fun setHiddenTrains(hiddenTrains: Set<String>) {
        _hiddenTrains.value = hiddenTrains
        _allTrains.value = _allTrains.value.map { train ->
            train.copy(isVisible = train.code !in hiddenTrains)
        }
    }
}

enum class SortMode(val key: StringResource) { TIME(Res.string.time), LINE(Res.string.line) }