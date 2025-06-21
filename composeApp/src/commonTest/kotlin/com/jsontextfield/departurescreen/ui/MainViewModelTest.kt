package com.jsontextfield.departurescreen.ui

import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.data.IGoTrainDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val baseTrain = Train(id = "0000")

    @Test
    fun `test sort by time`() = runTest {
        val goTrainDataSource = IGoTrainDataSource {
            List(4) {
                baseTrain.copy(
                    departureTime = Instant.fromEpochMilliseconds(10000 - it * 1000L),
                )
            }
        }
        val viewModel = MainViewModel(goTrainDataSource)
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
    fun `test sort by line`() = runTest {
        val goTrainDataSource = IGoTrainDataSource {
            listOf(
                baseTrain.copy(
                    code = "NY",
                    destination = "Don Mills",
                ),
                baseTrain.copy(
                    code = "AG",
                    destination = "Scarborough",
                ),
                baseTrain.copy(
                    code = "NY",
                    destination = "North York",
                ),
            )
        }

        val viewModel = MainViewModel(goTrainDataSource)
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