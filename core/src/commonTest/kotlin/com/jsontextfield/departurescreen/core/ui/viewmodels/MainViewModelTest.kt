@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.core.ui.viewmodels

import com.jsontextfield.departurescreen.core.data.fake.FakePreferencesRepository
import com.jsontextfield.departurescreen.core.data.fake.FakeTransitRepository
import com.jsontextfield.departurescreen.core.domain.GetSelectedStopUseCase
import com.jsontextfield.departurescreen.core.domain.SetFavouriteStopUseCase
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val baseTrip = Trip(id = "0000")
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSortByTime() = runTest(testDispatcher) {
        val goTrainDataSource = FakeTransitRepository()
        goTrainDataSource.trips = listOf(
            baseTrip.copy(
                departureTime = Instant.Companion.fromEpochMilliseconds(9000),
            ),
            baseTrip.copy(
                departureTime = Instant.Companion.fromEpochMilliseconds(8000),
            ),
            baseTrip.copy(
                departureTime = Instant.Companion.fromEpochMilliseconds(10000),
            ),
            baseTrip.copy(
                departureTime = Instant.Companion.fromEpochMilliseconds(7000),
            ),
        )

        val preferencesRepository = FakePreferencesRepository()
        preferencesRepository.setSortMode(SortMode.TIME)

        val mainViewModel = MainViewModel(
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
            getSelectedStopUseCase = GetSelectedStopUseCase(
                goTrainDataSource = goTrainDataSource,
                preferencesRepository = preferencesRepository,
            ),
            setFavouriteStopUseCase = SetFavouriteStopUseCase(
                preferencesRepository = preferencesRepository,
            )
        )
        advanceTimeBy(20000)

        mainViewModel.stop()

        val expectedResult = listOf(
            baseTrip.copy(
                departureTime = Instant.Companion.fromEpochMilliseconds(7000),
            ),
            baseTrip.copy(
                departureTime = Instant.Companion.fromEpochMilliseconds(8000),
            ),
            baseTrip.copy(
                departureTime = Instant.Companion.fromEpochMilliseconds(9000),
            ),
            baseTrip.copy(
                departureTime = Instant.Companion.fromEpochMilliseconds(10000),
            ),
        )

        assertEquals(SortMode.TIME, mainViewModel.uiState.value.sortMode)
        assertEquals(expectedResult, mainViewModel.uiState.value.allTrips)
    }

    @Test
    fun testSortByLine() = runTest(testDispatcher) {
        val goTrainDataSource = FakeTransitRepository()
        goTrainDataSource.trips = listOf(
            baseTrip.copy(
                code = "NY",
                destination = "North York",
            ),
            baseTrip.copy(
                code = "AG",
                destination = "Scarborough",
            ),
            baseTrip.copy(
                code = "NY",
                destination = "Don Mills",
            ),
        )

        val preferencesRepository = FakePreferencesRepository()
        preferencesRepository.setSortMode(SortMode.LINE)

        val mainViewModel = MainViewModel(
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
            getSelectedStopUseCase = GetSelectedStopUseCase(
                goTrainDataSource = goTrainDataSource,
                preferencesRepository = preferencesRepository,
            ),
            setFavouriteStopUseCase = SetFavouriteStopUseCase(
                preferencesRepository = preferencesRepository,
            )
        )
        advanceTimeBy(20000)

        mainViewModel.stop()

        val result = mainViewModel.uiState.value.allTrips

        val expectedResult = listOf(
            baseTrip.copy(
                code = "AG",
                destination = "Scarborough",
            ), baseTrip.copy(
                code = "NY",
                destination = "Don Mills",
            ), baseTrip.copy(
                code = "NY",
                destination = "North York",
            )
        )

        assertEquals(SortMode.LINE, mainViewModel.uiState.value.sortMode)
        assertEquals(expectedResult, result)
    }

    @Test
    fun testSetVisibleTrainsWhenTrainDeparts() = runTest(testDispatcher) {
        val goTrainDataSource = FakeTransitRepository()
        goTrainDataSource.trips = listOf(
            baseTrip.copy(
                code = "LW",
            ),
            baseTrip.copy(
                code = "BR",
            )
        )

        val preferencesRepository = FakePreferencesRepository()
        preferencesRepository.setVisibleTrains(setOf("LE"))

        val mainViewModel = MainViewModel(
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
            getSelectedStopUseCase = GetSelectedStopUseCase(
                goTrainDataSource = goTrainDataSource,
                preferencesRepository = preferencesRepository,
            ),
            setFavouriteStopUseCase = SetFavouriteStopUseCase(
                preferencesRepository = preferencesRepository,
            ),
        )

        val result = mainViewModel.uiState.value.visibleTrains

        mainViewModel.stop()

        assertEquals(emptySet(), result)
        assertEquals(true, "LE" !in result)
    }

    @Test
    fun testSetVisibleTrainsWhenTrainsHaveNotYetDeparted() = runTest(testDispatcher) {
        val goTrainDataSource = FakeTransitRepository()
        goTrainDataSource.trips = listOf(
            baseTrip.copy(
                code = "LW",
            ),
            baseTrip.copy(
                code = "BR",
            )
        )

        val preferencesRepository = FakePreferencesRepository()
        preferencesRepository.setVisibleTrains(setOf("LW"))

        val mainViewModel = MainViewModel(
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
            getSelectedStopUseCase = GetSelectedStopUseCase(
                goTrainDataSource = goTrainDataSource,
                preferencesRepository = preferencesRepository,
            ),
            setFavouriteStopUseCase = SetFavouriteStopUseCase(
                preferencesRepository = preferencesRepository,
            ),
        )
        advanceTimeBy(20000)

        mainViewModel.stop()

        val result = mainViewModel.uiState.value.visibleTrains

        assertEquals(true, "LW" in result)
    }

    @Test
    fun testSetTheme() = runTest {
        val goTrainDataSource = FakeTransitRepository()
        val preferencesRepository = FakePreferencesRepository()

        val mainViewModel = MainViewModel(
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
            getSelectedStopUseCase = GetSelectedStopUseCase(
                goTrainDataSource = goTrainDataSource,
                preferencesRepository = preferencesRepository,
            ),
            setFavouriteStopUseCase = SetFavouriteStopUseCase(
                preferencesRepository = preferencesRepository,
            ),
        )

        assertEquals(ThemeMode.DEFAULT, mainViewModel.uiState.value.theme)

        mainViewModel.setTheme(ThemeMode.LIGHT)
        advanceTimeBy(20000)

        mainViewModel.stop()

        val results = mainViewModel.uiState.value
        // read the current ui state value
        assertEquals(ThemeMode.LIGHT, results.theme)
    }
}