package com.jsontextfield.departurescreen.core.ui.viewmodels

import com.jsontextfield.departurescreen.core.data.fake.FakePreferencesRepository
import com.jsontextfield.departurescreen.core.data.fake.FakeTransitRepository
import com.jsontextfield.departurescreen.core.domain.GetSelectedStopUseCase
import com.jsontextfield.departurescreen.core.domain.SetFavouriteStopUseCase
import com.jsontextfield.departurescreen.core.entities.Stop
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
class StopsViewModelTest {

    lateinit var preferencesRepository: FakePreferencesRepository
    lateinit var goTrainDataSource: FakeTransitRepository
    lateinit var getSelectedStopUseCase: GetSelectedStopUseCase

    lateinit var setFavouriteStopUseCase: SetFavouriteStopUseCase
    lateinit var stopsViewModel: StopsViewModel
    val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        preferencesRepository = FakePreferencesRepository()
        goTrainDataSource = FakeTransitRepository()
        getSelectedStopUseCase = GetSelectedStopUseCase(
            preferencesRepository,
            goTrainDataSource,
        )
        setFavouriteStopUseCase = SetFavouriteStopUseCase(
            preferencesRepository,
        )
        stopsViewModel = StopsViewModel(
            getSelectedStopUseCase = getSelectedStopUseCase,
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
            setFavouriteStopUseCase = setFavouriteStopUseCase,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSetSelectedStop() = runTest(testDispatcher) {
        val stop = Stop(
            name = "Test",
            code = "TST",
        )
        goTrainDataSource.stops = listOf(
            Stop(
                name = "Union Station",
                code = "UN",
            ),
            Stop(
                name = "Test",
                code = "TST",
            )
        )
        advanceUntilIdle()
        assertEquals(true, goTrainDataSource.stops.isNotEmpty())
        assertEquals(true, "UN" in (stopsViewModel.uiState.value.selectedStop?.code ?: ""))
        stopsViewModel.setSelectedStop(stop)
        advanceUntilIdle()
        assertEquals(stop.code.split(",").first(), preferencesRepository.getSelectedStopCode().first())
        assertEquals(stop, getSelectedStopUseCase().first())
        stopsViewModel = StopsViewModel(
            getSelectedStopUseCase = getSelectedStopUseCase,
            setFavouriteStopUseCase = setFavouriteStopUseCase,
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
        )
        advanceUntilIdle()
        val selectedStop = stopsViewModel.uiState.value.selectedStop
        assertEquals(stop, selectedStop)
    }
}