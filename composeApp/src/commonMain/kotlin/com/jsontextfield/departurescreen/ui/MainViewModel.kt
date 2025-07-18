package com.jsontextfield.departurescreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.data.IPreferencesRepository
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

class MainViewModel(
    private val goTrainDataSource: IGoTrainDataSource,
    private val preferencesRepository: IPreferencesRepository,
) : ViewModel() {

    private var _allTrains: MutableStateFlow<List<Train>> = MutableStateFlow(emptyList())
    val allTrains: StateFlow<List<Train>> = _allTrains.asStateFlow()

    private var _hiddenTrains: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    val hiddenTrains: StateFlow<Set<String>> = _hiddenTrains.asStateFlow()

    private var _timeRemaining: MutableStateFlow<Int> = MutableStateFlow(0)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private var _sortMode: MutableStateFlow<SortMode> = MutableStateFlow(SortMode.TIME)
    val sortMode: StateFlow<SortMode> = _sortMode.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            _hiddenTrains.value = preferencesRepository.getHiddenTrains() ?: emptySet()
            _sortMode.value = preferencesRepository.getSortMode() ?: SortMode.TIME
        }
        timerJob = timerJob ?: viewModelScope.launch {
            while (true) {
                if (timeRemaining.value <= 0) {
                    try {
                        _allTrains.value = goTrainDataSource.getTrains()
                        setHiddenTrains(_hiddenTrains.value)
                        setSortMode(_sortMode.value)
                        _timeRemaining.value = 20_000
                    } catch (_: IOException) {
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
        viewModelScope.launch {
            preferencesRepository.setSortMode(mode)
        }
    }

    fun setHiddenTrains(hiddenTrains: Set<String>) {
        _hiddenTrains.value = hiddenTrains
        _allTrains.value = _allTrains.value.map { train ->
            train.copy(isVisible = train.code in hiddenTrains || hiddenTrains.isEmpty())
        }
        viewModelScope.launch {
            preferencesRepository.setHiddenTrains(hiddenTrains)
        }
    }
}

enum class SortMode(val key: StringResource) { TIME(Res.string.time), LINE(Res.string.line) }