package com.jsontextfield.departurescreen.core.ui.viewmodels

import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.data.fake.FakePreferencesRepository
import com.jsontextfield.departurescreen.core.data.fake.FakeTransitRepository
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class AlertDetailsViewModelTest {
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
     * GIVEN a valid alert ID and repository with the alert
     * WHEN loadAlert is called
     * THEN uiState status is LOADED and the alert is successfully retrieved
     */
    @Test
    fun testLoadAlertSuccess() = runTest(testDispatcher) {
        val transitRepository = FakeTransitRepository()
        val testAlert = Alert(id = "alert-123", bodyEn = "Service update")
        val customRepository = object : ITransitRepository by transitRepository {
            override fun getAllAlerts(language: String) = flowOf(listOf(testAlert))
        }

        val preferencesRepository = FakePreferencesRepository()
        val viewModel = AlertDetailsViewModel(
            transitRepository = customRepository,
            preferencesRepository = preferencesRepository,
            alertId = "alert-123",
        )

        viewModel.loadAlert("en")
        advanceUntilIdle()

        assertEquals(Status.LOADED, viewModel.uiState.value.status)
        assertNotNull(viewModel.uiState.value.alert)
        assertEquals("alert-123", viewModel.uiState.value.alert?.id)
    }

    /**
     * GIVEN a non-existent alert ID
     * WHEN loadAlert is called
     * THEN uiState status is ERROR
     */
    @Test
    fun testLoadAlertNotFound() = runTest(testDispatcher) {
        val transitRepository = FakeTransitRepository()
        val preferencesRepository = FakePreferencesRepository()

        val viewModel = AlertDetailsViewModel(
            transitRepository = transitRepository,
            preferencesRepository = preferencesRepository,
            alertId = "non-existent",
        )

        viewModel.loadAlert("en")
        advanceUntilIdle()

        assertEquals(Status.ERROR, viewModel.uiState.value.status)
    }

    /**
     * GIVEN a time format preference set in preferences repository
     * WHEN AlertDetailsViewModel is initialized
     * THEN uiState timeFormat reflects the preference
     */
    @Test
    fun testTimeFormatObservation() = runTest(testDispatcher) {
        val transitRepository = FakeTransitRepository()
        val preferencesRepository = FakePreferencesRepository()
        preferencesRepository.setTimeFormat(TimeFormat.TWELVE_HOUR)

        val viewModel = AlertDetailsViewModel(
            transitRepository = transitRepository,
            preferencesRepository = preferencesRepository,
            alertId = "123",
        )

        advanceUntilIdle()
        assertEquals(TimeFormat.TWELVE_HOUR, viewModel.uiState.value.timeFormat)
    }
}
