@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.core.ui.viewmodels

import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.data.fake.FakePreferencesRepository
import com.jsontextfield.departurescreen.core.data.fake.FakeTransitRepository
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.Schedule
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * GIVEN a transit repository configured to throw errors
     * WHEN TripDetailsViewModel loads data
     * THEN overall status reflects an ERROR state
     */
    @Test
    fun testLoadDataError() = runTest(testDispatcher) {
        val transitRepository = FakeTransitRepository()
        transitRepository.shouldThrowError = true
        val customRepository = object : ITransitRepository by transitRepository {
            override fun getAllAlerts(language: String) = flow<List<Alert>> { throw Exception("Error") }
        }
        val preferencesRepository = FakePreferencesRepository()

        val viewModel = TripDetailsViewModel(
            preferencesRepository = preferencesRepository,
            transitRepository = customRepository,
            selectedStop = "UN",
            stopCode = "UN",
            tripId = "123",
            lineCode = "LW",
            destination = "Niagara Falls",
        )

        advanceUntilIdle()
        assertEquals(Status.ERROR, viewModel.uiState.value.status)
    }

    /**
     * GIVEN a valid transit repository
     * WHEN TripDetailsViewModel loads data
     * THEN overall status and stopsStatus are LOADED
     */
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
            destination = "Niagara Falls",
        )

        advanceUntilIdle()
        assertEquals(Status.LOADED, viewModel.uiState.value.status)
        assertEquals(Status.LOADED, viewModel.uiState.value.stopsStatus)
    }

    /**
     * GIVEN alerts affecting specific lines and stops
     * WHEN loadAlerts is called
     * THEN alerts list is filtered to match the trip's line code and selected stop
     */
    @Test
    fun testLoadAlertsFiltering() = runTest(testDispatcher) {
        val transitRepository = FakeTransitRepository()
        val alertMatchingLine = Alert(id = "1", affectedLines = listOf("LW"), bodyEn = "LW Alert")
        val alertMatchingStop = Alert(id = "2", affectedStops = listOf("UN"), bodyEn = "UN Alert")
        val alertNonMatching = Alert(id = "3", affectedLines = listOf("MI"), bodyEn = "MI Alert")

        val customRepository = object : ITransitRepository by transitRepository {
            override fun getAllAlerts(language: String) = flowOf(listOf(alertMatchingLine, alertMatchingStop, alertNonMatching))
        }

        val preferencesRepository = FakePreferencesRepository()
        val viewModel = TripDetailsViewModel(
            preferencesRepository = preferencesRepository,
            transitRepository = customRepository,
            selectedStop = "UN",
            stopCode = "UN",
            tripId = "123",
            lineCode = "LW",
            destination = "Niagara Falls",
        )

        viewModel.loadAlerts("en")
        advanceUntilIdle()

        assertEquals(Status.LOADED, viewModel.uiState.value.alertsStatus)
        val alerts = viewModel.uiState.value.alerts
        assertEquals(2, alerts.size)
        assertEquals(true, alerts.any { it.id == "1" })
        assertEquals(true, alerts.any { it.id == "2" })
        assertEquals(false, alerts.any { it.id == "3" })
    }

    /**
     * GIVEN an UP Express line code and schedule repository response
     * WHEN TripDetailsViewModel loads schedule data
     * THEN stops status is LOADED with UP schedule items
     */
    @Test
    fun testUpExpressTripScheduleLoading() = runTest(testDispatcher) {
        val transitRepository = FakeTransitRepository()
        val customRepository = object : ITransitRepository by transitRepository {
            override suspend fun getUPExpressTripSchedule(id: String): List<Schedule> {
                return listOf(Schedule(name = "Union Pearson", code = "UP"))
            }
        }
        val preferencesRepository = FakePreferencesRepository()

        val viewModel = TripDetailsViewModel(
            preferencesRepository = preferencesRepository,
            transitRepository = customRepository,
            selectedStop = "UN",
            stopCode = "UN",
            tripId = "up-123",
            lineCode = "UP",
            destination = "Pearson",
        )

        advanceUntilIdle()
        assertEquals(Status.LOADED, viewModel.uiState.value.stopsStatus)
        assertEquals(1, viewModel.uiState.value.stops.size)
        assertEquals("Union Pearson", viewModel.uiState.value.stops.first().name)
    }

    /**
     * GIVEN a time format preference set in preferences repository
     * WHEN TripDetailsViewModel is initialized
     * THEN uiState timeFormat reflects the preference
     */
    @Test
    fun testTimeFormatObservation() = runTest(testDispatcher) {
        val transitRepository = FakeTransitRepository()
        val preferencesRepository = FakePreferencesRepository()
        preferencesRepository.setTimeFormat(TimeFormat.TWENTY_FOUR_HOUR)

        val viewModel = TripDetailsViewModel(
            preferencesRepository = preferencesRepository,
            transitRepository = transitRepository,
            selectedStop = "UN",
            stopCode = "UN",
            tripId = "123",
            lineCode = "LW",
            destination = "Niagara Falls",
        )

        advanceUntilIdle()
        assertEquals(TimeFormat.TWENTY_FOUR_HOUR, viewModel.uiState.value.timeFormat)
    }
}
