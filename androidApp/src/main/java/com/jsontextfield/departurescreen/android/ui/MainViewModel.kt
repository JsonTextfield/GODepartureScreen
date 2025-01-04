package com.jsontextfield.departurescreen.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.data.GoTrainDataSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private var _trains: MutableStateFlow<List<Train>> = MutableStateFlow(emptyList())
    val trains: StateFlow<List<Train>> = _trains.asStateFlow()

    private var _timeRemaining: MutableStateFlow<Int> = MutableStateFlow(0)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private var timerJob: Job? = null

    fun start(apiKey: String) {
        timerJob = timerJob ?: viewModelScope.launch {
            while (true) {
                if (timeRemaining.value <= 0) {
                    _trains.value = GoTrainDataSource().getTrains(apiKey)
                    _timeRemaining.value = 30_000
                }
                else {
                    delay(1000)
                    _timeRemaining.value -= 1000
                }
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        timerJob = null
        super.onCleared()
    }
}