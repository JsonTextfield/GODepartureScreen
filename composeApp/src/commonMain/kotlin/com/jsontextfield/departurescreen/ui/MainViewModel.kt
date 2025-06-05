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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class MainViewModel(private val goTrainDataSource: IGoTrainDataSource) : ViewModel() {

    private var _trains: MutableStateFlow<List<Train>> = MutableStateFlow(emptyList())
    val trains: StateFlow<List<Train>> = _trains.asStateFlow()

    private var _displayedTrains: MutableStateFlow<List<Train>> = MutableStateFlow(emptyList())
    val displayedTrains: StateFlow<List<Train>> = _displayedTrains.asStateFlow()

    private var _hiddenTrains: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    val hiddenTrains: StateFlow<Set<String>> = _hiddenTrains.asStateFlow()

    private var _sortMode: MutableStateFlow<SortMode> = MutableStateFlow(SortMode.TIME)
    val sortMode: StateFlow<SortMode> = _sortMode.asStateFlow()

    var showFilterDialog by mutableStateOf(false)

    fun getTrains() {
        viewModelScope.launch {
            _trains.value = goTrainDataSource.getTrains()
            updateDisplayedTrains()
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
                    SortMode.LINE -> compareBy({ it.code }, { it.destination })
                }
            )
    }
}

enum class SortMode(val key: StringResource) { TIME(Res.string.time), LINE(Res.string.line) }