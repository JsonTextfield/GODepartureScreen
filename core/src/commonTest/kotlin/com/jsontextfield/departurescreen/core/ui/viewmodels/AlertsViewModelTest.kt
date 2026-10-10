package com.jsontextfield.departurescreen.core.ui.viewmodels

import com.jsontextfield.departurescreen.core.data.fake.FakePreferencesRepository
import com.jsontextfield.departurescreen.core.data.fake.FakeTransitRepository
import com.jsontextfield.departurescreen.core.ui.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class AlertsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        // Reset the base train before each test
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        // Reset the main dispatcher after each test
        Dispatchers.resetMain()
    }

    /**
     * GIVEN an AlertsViewModel instance
     * WHEN refresh is called
     * THEN uiState indicates refreshing state and completes successfully
     */
    @Test
    fun testRefresh() = runTest(testDispatcher) {
        val goTrainDataSource = FakeTransitRepository()
        val preferencesRepository = FakePreferencesRepository()
        val alertsViewModel = AlertsViewModel(
            goTrainDataSource,
            preferencesRepository,
        )

        advanceUntilIdle()
        assertEquals(Status.LOADED, alertsViewModel.uiState.value.status)

        alertsViewModel.refresh()
        assertEquals(Status.LOADED, alertsViewModel.uiState.value.status)
        assertEquals(true, alertsViewModel.uiState.value.isRefreshing)

        advanceUntilIdle()
        assertEquals(false, alertsViewModel.uiState.value.isRefreshing)
    }

    /**
     * GIVEN an AlertsViewModel instance
     * WHEN loadData is called
     * THEN uiState transitions through LOADING to LOADED status
     */
    @Test
    fun testLoadData() = runTest(testDispatcher) {
        val transitRepository = FakeTransitRepository()
        val preferencesRepository = FakePreferencesRepository()
        val alertsViewModel = AlertsViewModel(
            transitRepository = transitRepository,
            preferencesRepository = preferencesRepository,
        )

        advanceUntilIdle()
        assertEquals(Status.LOADED, alertsViewModel.uiState.value.status)

        alertsViewModel.loadData()
        assertEquals(Status.LOADING, alertsViewModel.uiState.value.status)

        advanceUntilIdle()
        assertEquals(Status.LOADED, alertsViewModel.uiState.value.status)

    }

    /**
     * GIVEN selected lines and unread status flag
     * WHEN setFilter is called
     * THEN uiState reflects the updated line filter and unread selection
     */
    @Test
    fun testSetFilter() = runTest(testDispatcher) {
        val transitRepository = FakeTransitRepository()
        val preferencesRepository = FakePreferencesRepository()
        val alertsViewModel = AlertsViewModel(
            transitRepository = transitRepository,
            preferencesRepository = preferencesRepository,
        )

        advanceUntilIdle()

        val selectedLines = setOf("LE", "LW")
        alertsViewModel.setFilter(selectedLines, true)

        advanceUntilIdle()

        assertEquals(selectedLines, alertsViewModel.uiState.value.selectedLines)
        assertEquals(true, alertsViewModel.uiState.value.isUnreadSelected)
    }

    /**
     * GIVEN pre-set alert line and unread preferences
     * WHEN AlertsViewModel is initialized
     * THEN uiState correctly loads persisted preferences
     */
    @Test
    fun testPersistence() = runTest(testDispatcher) {
        val transitRepository = FakeTransitRepository()
        val preferencesRepository = FakePreferencesRepository()

        // Pre-set some preferences
        val preSelectedLines = setOf("GT", "RH")
        preferencesRepository.setVisibleAlertLines(preSelectedLines)
        preferencesRepository.setIsUnreadAlertsSelected(true)

        val alertsViewModel = AlertsViewModel(
            transitRepository = transitRepository,
            preferencesRepository = preferencesRepository,
        )

        advanceUntilIdle()

        assertEquals(preSelectedLines, alertsViewModel.uiState.value.selectedLines)
        assertEquals(true, alertsViewModel.uiState.value.isUnreadSelected)
    }
}
