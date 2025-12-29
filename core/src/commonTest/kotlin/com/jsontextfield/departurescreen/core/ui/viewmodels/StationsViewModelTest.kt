package com.jsontextfield.departurescreen.core.ui.viewmodels

import com.jsontextfield.departurescreen.core.data.fake.FakeGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.fake.FakePreferencesRepository
import com.jsontextfield.departurescreen.core.domain.GetSelectedStationUseCase
import com.jsontextfield.departurescreen.core.domain.SetFavouriteStationUseCase
import com.jsontextfield.departurescreen.core.entities.Station
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

@OptIn(ExperimentalCoroutinesApi::class)
class StationsViewModelTest {

    lateinit var preferencesRepository: FakePreferencesRepository
    lateinit var goTrainDataSource: FakeGoTrainDataSource
    lateinit var getSelectedStationUseCase: GetSelectedStationUseCase

    lateinit var setFavouriteStationUseCase: SetFavouriteStationUseCase
    lateinit var stationsViewModel: StationsViewModel
    val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        preferencesRepository = FakePreferencesRepository()
        goTrainDataSource = FakeGoTrainDataSource()
        getSelectedStationUseCase = GetSelectedStationUseCase(
            preferencesRepository,
            goTrainDataSource,
        )
        setFavouriteStationUseCase = SetFavouriteStationUseCase(
            preferencesRepository,
        )
        stationsViewModel = StationsViewModel(
            getSelectedStationUseCase = getSelectedStationUseCase,
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
            setFavouriteStationUseCase = setFavouriteStationUseCase,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSetSelectedStation() = runTest(testDispatcher) {
        val station = Station(
            name = "Test",
            code = "TST",
        )
        goTrainDataSource.stations = listOf(
            Station(
                name = "Union Station",
                code = "UN",
            ),
            Station(
                name = "Test",
                code = "TST",
            )
        )
        advanceUntilIdle()
        assertEquals(true, goTrainDataSource.stations.isNotEmpty())
        assertEquals(true, "UN" in (stationsViewModel.uiState.value.selectedStation?.code ?: ""))
        stationsViewModel.setSelectedStation(station)
        advanceUntilIdle()
        assertEquals(station.code.split(",").first(), preferencesRepository.getSelectedStationCode().first())
        assertEquals(station, getSelectedStationUseCase().first())
        stationsViewModel = StationsViewModel(
            getSelectedStationUseCase = getSelectedStationUseCase,
            setFavouriteStationUseCase = setFavouriteStationUseCase,
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
        )
        advanceUntilIdle()
        val selectedStation = stationsViewModel.uiState.value.selectedStation
        assertEquals(station, selectedStation)
    }
}