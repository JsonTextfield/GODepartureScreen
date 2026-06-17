@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.core.ui.viewmodels

import com.jsontextfield.departurescreen.core.data.fake.FakePreferencesRepository
import com.jsontextfield.departurescreen.core.data.fake.FakeTransitRepository
import com.jsontextfield.departurescreen.core.ui.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
class TripDetailsViewModelTest {
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
    fun testLoadDataError() = runTest(testDispatcher) {
        val transitRepository = FakeTransitRepository()
        transitRepository.shouldThrowError = true
        val preferencesRepository = FakePreferencesRepository()

        val viewModel = TripDetailsViewModel(
            preferencesRepository = preferencesRepository,
            transitRepository = transitRepository,
            selectedStop = "UN",
            stopCode = "UN",
            tripId = "123",
            lineCode = "LW",
            directionCode = "W",
            direction = "W",
            destination = "Niagara Falls",
        )

        assertEquals(Status.ERROR, viewModel.uiState.value.status)
    }

    @Test
    fun testLoadDataSuccess() = runTest(testDispatcher) {
        val transitRepository = FakeTransitRepository()
        val preferencesRepository = FakePreferencesRepository()

        val viewModel = TripDetailsViewModel(
            preferencesRepository = preferencesRepository,
            transitRepository = transitRepository,
            selectedStop = "UN",
            stopCode = "UN",
            tripId = "123",
            lineCode = "LW",
            directionCode = "W",
            direction = "W",
            destination = "Niagara Falls",
        )

        assertEquals(Status.LOADED, viewModel.uiState.value.status)
    }
}
