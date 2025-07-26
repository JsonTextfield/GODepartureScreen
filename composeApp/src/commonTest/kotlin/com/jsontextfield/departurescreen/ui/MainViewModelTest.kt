package com.jsontextfield.departurescreen.ui

import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.data.FakeGoTrainDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val baseTrain = Train(id = "0000")
    private lateinit var dispatcher: TestDispatcher

    @BeforeTest
    fun setup() {
        // Reset the base train before each test
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        // Reset the main dispatcher after each test
        Dispatchers.resetMain()
    }

    @Test
    fun `test sort by time`() = runTest(dispatcher) {
        val goTrainDataSource = FakeGoTrainDataSource()
        goTrainDataSource.trains = listOf(
            baseTrain.copy(
                departureTime = Instant.fromEpochMilliseconds(9000),
            ),
            baseTrain.copy(
                departureTime = Instant.fromEpochMilliseconds(8000),
            ),
            baseTrain.copy(
                departureTime = Instant.fromEpochMilliseconds(10000),
            ),
            baseTrain.copy(
                departureTime = Instant.fromEpochMilliseconds(7000),
            ),
        )

        val preferencesRepository = FakePreferencesRepository()
        preferencesRepository.setSortMode(SortMode.TIME)

        val mainViewModel = MainViewModel(
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
        )
        mainViewModel.stop()

        val expectedResult = listOf(
            baseTrain.copy(
                departureTime = Instant.fromEpochMilliseconds(7000),
            ),
            baseTrain.copy(
                departureTime = Instant.fromEpochMilliseconds(8000),
            ),
            baseTrain.copy(
                departureTime = Instant.fromEpochMilliseconds(9000),
            ),
            baseTrain.copy(
                departureTime = Instant.fromEpochMilliseconds(10000),
            ),
        )

        assertEquals(SortMode.TIME, mainViewModel.uiState.value.sortMode)
        assertEquals(expectedResult, mainViewModel.uiState.value.allTrains)
    }

    @Test
    fun `test sort by line`() = runTest(dispatcher) {
        val goTrainDataSource = FakeGoTrainDataSource()
        goTrainDataSource.trains = listOf(
            baseTrain.copy(
                code = "NY",
                destination = "North York",
            ),
            baseTrain.copy(
                code = "AG",
                destination = "Scarborough",
            ),
            baseTrain.copy(
                code = "NY",
                destination = "Don Mills",
            ),
        )

        val preferencesRepository = FakePreferencesRepository()
        preferencesRepository.setSortMode(SortMode.LINE)

        val mainViewModel = MainViewModel(
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
        )
        mainViewModel.stop()

        val result = mainViewModel.uiState.value.allTrains

        val expectedResult = listOf(
            baseTrain.copy(
                code = "AG",
                destination = "Scarborough",
            ), baseTrain.copy(
                code = "NY",
                destination = "Don Mills",
            ), baseTrain.copy(
                code = "NY",
                destination = "North York",
            )
        )

        assertEquals(SortMode.LINE, mainViewModel.uiState.value.sortMode)
        assertEquals(expectedResult, result)
    }

    @Test
    fun `test set visible trains when train departs`() = runTest(dispatcher) {
        val goTrainDataSource = FakeGoTrainDataSource()
        goTrainDataSource.trains = listOf(
            baseTrain.copy(
                code = "LW",
            ),
            baseTrain.copy(
                code = "BR",
            )
        )

        val preferencesRepository = FakePreferencesRepository()
        preferencesRepository.setVisibleTrains(setOf("LE"))

        val mainViewModel = MainViewModel(
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
        )
        mainViewModel.stop()

        val result = mainViewModel.uiState.value.visibleTrains

        assertEquals(emptySet(), result)
        assertEquals(true, "LE" !in result)
    }

    @Test
    fun `test set visible trains when trains have not yet departed`() = runTest(dispatcher) {
        val goTrainDataSource = FakeGoTrainDataSource()
        goTrainDataSource.trains = listOf(
            baseTrain.copy(
                code = "LW",
            ),
            baseTrain.copy(
                code = "BR",
            )
        )

        val preferencesRepository = FakePreferencesRepository()
        preferencesRepository.setVisibleTrains(setOf("LW"))

        val mainViewModel = MainViewModel(
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
        )
        mainViewModel.stop()

        val result = mainViewModel.uiState.value.visibleTrains

        assertEquals(true, "LW" in result)
    }

    @Test
    fun `test setTheme`() = runTest {
        val goTrainDataSource = FakeGoTrainDataSource()
        val preferencesRepository = FakePreferencesRepository()
        preferencesRepository.setTheme(ThemeMode.DARK)

        val mainViewModel = MainViewModel(
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
        )
        mainViewModel.stop()

        val result = mainViewModel.uiState.value.theme
        assertEquals(ThemeMode.DARK, result)

        mainViewModel.setTheme(ThemeMode.LIGHT)
        advanceUntilIdle()
        assertEquals(ThemeMode.LIGHT, mainViewModel.uiState.value.theme)
    }
}