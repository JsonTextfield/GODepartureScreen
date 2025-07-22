package com.jsontextfield.departurescreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.Alert
import com.jsontextfield.departurescreen.data.IGoTrainDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertViewModel(
    val goTrainDataSource: IGoTrainDataSource,
) : ViewModel() {
    private val _informationAlerts: MutableStateFlow<List<Alert>> = MutableStateFlow(emptyList())
    val informationAlerts: StateFlow<List<Alert>> = _informationAlerts.asStateFlow()

    private val _serviceAlerts: MutableStateFlow<List<Alert>> = MutableStateFlow(emptyList())
    val serviceAlerts: StateFlow<List<Alert>> = _serviceAlerts.asStateFlow()

    fun loadAlerts() {
        viewModelScope.launch {
            async { _serviceAlerts.value = goTrainDataSource.getServiceAlerts() }
            async { _informationAlerts.value = goTrainDataSource.getInformationAlerts() }
        }
    }
}