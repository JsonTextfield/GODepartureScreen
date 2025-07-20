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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import org.jetbrains.compose.resources.StringResource

class MainViewModel(
    private val goTrainDataSource: IGoTrainDataSource,
    private val preferencesRepository: IPreferencesRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val _timeRemaining: MutableStateFlow<Int> = MutableStateFlow(0)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    visibleTrains = preferencesRepository.getVisibleTrains() ?: emptySet(),
                    sortMode = preferencesRepository.getSortMode() ?: SortMode.TIME
                )
            }
            timerJob = timerJob ?: viewModelScope.launch {
                while (true) {
                    if (timeRemaining.value <= 0) {
                        try {
                            _uiState.update {
                                it.copy(
                                    allTrains = goTrainDataSource.getTrains(),
                                )
                            }
                            setVisibleTrains(uiState.value.visibleTrains)
                            setSortMode(uiState.value.sortMode)
                            _timeRemaining.value = 20_000
                        } catch (_: IOException) {
                            _timeRemaining.value = 1000
                        }
                    } else {
                        delay(1000)
                        _timeRemaining.value -= 1000
                    }
                }
            }
        }
    }

    fun setSortMode(mode: SortMode) {
        viewModelScope.launch {
            preferencesRepository.setSortMode(mode)
            _uiState.update { uiState ->
                uiState.copy(
                    sortMode = mode,
                    allTrains = uiState.allTrains.sortedWith(
                        when (mode) {
                            SortMode.TIME -> compareBy { it.departureTime }
                            SortMode.LINE -> compareBy({ it.code }, { it.destination })
                        }
                    ),
                )
            }
        }
    }

    fun setVisibleTrains(selectedTrains: Set<String>) {
        viewModelScope.launch {
            // Ensure that the visible trains are a subset of all trains
            val trainCodes = uiState.value.allTrains.map { it.code }.toSet() intersect selectedTrains
            preferencesRepository.setVisibleTrains(trainCodes)
            _uiState.update {
                it.copy(
                    visibleTrains = trainCodes,
                    allTrains = it.allTrains.map { train ->
                        train.copy(isVisible = train.code in trainCodes || trainCodes.isEmpty())
                    }
                )
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}

enum class SortMode(val key: StringResource) {
    TIME(Res.string.time),
    LINE(Res.string.line),
}

data class UIState(
    val allTrains: List<Train> = emptyList(),
    val visibleTrains: Set<String> = emptySet(),
    val sortMode: SortMode = SortMode.TIME,
)