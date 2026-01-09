package com.jsontextfield.departurescreen.core.ui.viewmodels

import com.jsontextfield.departurescreen.core.data.fake.FakeGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.fake.FakePreferencesRepository
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

    @Test
    fun testRefresh() = runTest(testDispatcher) {
        val goTrainDataSource = FakeGoTrainDataSource()
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

    @Test
    fun testLoadData() = runTest(testDispatcher) {
        val goTrainDataSource = FakeGoTrainDataSource()
        val preferencesRepository = FakePreferencesRepository()
        val alertsViewModel = AlertsViewModel(
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
        )

        advanceUntilIdle()
        assertEquals(Status.LOADED, alertsViewModel.uiState.value.status)

        alertsViewModel.loadData()
        assertEquals(Status.LOADING, alertsViewModel.uiState.value.status)

        advanceUntilIdle()
        assertEquals(Status.LOADED, alertsViewModel.uiState.value.status)

    }
}