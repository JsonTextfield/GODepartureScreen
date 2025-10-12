@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jsontextfield.departurescreen.ui

import com.jsontextfield.departurescreen.core.data.FakeGoTrainDataSource
import com.jsontextfield.departurescreen.core.domain.DepartureScreenUseCase
import com.jsontextfield.departurescreen.core.entities.CombinedStation
import com.jsontextfield.departurescreen.core.entities.Station
import com.jsontextfield.departurescreen.core.ui.viewmodels.StationsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StationsViewModelTest {

    lateinit var preferencesRepository: FakePreferencesRepository
    lateinit var goTrainDataSource: FakeGoTrainDataSource
    lateinit var departureScreenUseCase: DepartureScreenUseCase

    lateinit var stationsViewModel: StationsViewModel
    val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        preferencesRepository = FakePreferencesRepository()
        goTrainDataSource = FakeGoTrainDataSource()
        departureScreenUseCase = DepartureScreenUseCase(
            preferencesRepository,
            goTrainDataSource,
        )
        stationsViewModel = StationsViewModel(
            departureScreenUseCase = departureScreenUseCase,
            preferencesRepository = preferencesRepository,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test setSelectedStation`() = runTest(testDispatcher) {
        val station = CombinedStation(
            name = "Test",
            codes = setOf("TST"),
            types = setOf("Bus Stop"),
        )
        goTrainDataSource.stations = listOf(
            Station(
                name = "Union Station",
                code = "UN",
                type = "Train Station",
            ),
            Station(
                name = "Test",
                code = "TST",
                type = "Bus Stop",
            )
        )
        advanceUntilIdle()
        assertEquals(true, goTrainDataSource.stations.isNotEmpty())
        assertEquals(true, "UN" in (stationsViewModel.uiState.value.selectedStation?.codes ?: emptySet()))
        stationsViewModel.setSelectedStation(station)
        advanceUntilIdle()
        assertEquals(station.codes.first(), preferencesRepository.getSelectedStationCode().first())
        assertEquals(station, departureScreenUseCase.getSelectedStation().first())
        stationsViewModel = StationsViewModel(
            departureScreenUseCase = departureScreenUseCase,
            preferencesRepository = preferencesRepository,
        )
        advanceUntilIdle()
        val selectedStation = stationsViewModel.uiState.value.selectedStation
        assertEquals(station, selectedStation)
    }
}