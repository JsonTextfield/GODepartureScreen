package com.jsontextfield.departurescreen.ui

import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.data.FakeGoTrainDataSource
import com.jsontextfield.departurescreen.data.IGoTrainDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
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
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test sort by time`() = runTest(dispatcher) {
        val goTrainDataSource = FakeGoTrainDataSource()
        val preferencesRepository = FakePreferencesRepository()

        val viewModel = MainViewModel(
            goTrainDataSource,
            preferencesRepository,
        )
        advanceUntilIdle()

        viewModel.setSortMode(SortMode.TIME)
        advanceUntilIdle()

        val expectedResult = listOf(
            baseTrain.copy(
                departureTime = Instant.fromEpochMilliseconds(7000),
            ), baseTrain.copy(
                departureTime = Instant.fromEpochMilliseconds(8000),
            ), baseTrain.copy(
                departureTime = Instant.fromEpochMilliseconds(9000),
            ), baseTrain.copy(
                departureTime = Instant.fromEpochMilliseconds(10000),
            )
        )

        assertEquals(expectedResult, viewModel.allTrains.value)
    }

    @Test
    fun `test sort by line`() = runTest(dispatcher) {
        val goTrainDataSource = FakeGoTrainDataSource()
        val preferencesRepository = FakePreferencesRepository()

        val viewModel = MainViewModel(
            goTrainDataSource,
            preferencesRepository,
        )
        advanceUntilIdle()

        viewModel.setSortMode(SortMode.LINE)
        advanceUntilIdle()

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

        assertEquals(expectedResult, viewModel.allTrains.value)
    }
}