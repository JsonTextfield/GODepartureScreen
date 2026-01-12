@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jsontextfield.departurescreen.core.domain

import com.jsontextfield.departurescreen.core.data.fake.FakeGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.fake.FakePreferencesRepository
import com.jsontextfield.departurescreen.core.entities.Stop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetSelectedStopUseCaseTest {

    private lateinit var fakeGoTrainDataSource: FakeGoTrainDataSource
    private lateinit var fakePreferencesRepository: FakePreferencesRepository
    private lateinit var getSelectedStopUseCase: GetSelectedStopUseCase

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        fakeGoTrainDataSource = FakeGoTrainDataSource()
        fakePreferencesRepository = FakePreferencesRepository()
        getSelectedStopUseCase = GetSelectedStopUseCase(
            preferencesRepository = fakePreferencesRepository,
            goTrainDataSource = fakeGoTrainDataSource,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * test invoke returns stop for stop code if it exists
     */
    @Test
    fun test1() = runTest {
        val stop = Stop(
            code = "JB",
            name = "Test Stop",
        )
        fakeGoTrainDataSource.stops = listOf(stop)

        val result = getSelectedStopUseCase(stop.code).first()

        assertEquals(stop, result)
    }

    /**
     * test invoke returns stop from preferences if stop code is null and preferences exist
     */
    @Test
    fun test2() =
        runTest {
            val preferredStop = Stop(
                code = "PS",
                name = "Preferred Stop",
            )
            val otherStop = Stop(
                code = "JB",
                name = "Test Stop",
            )
            fakeGoTrainDataSource.stops = listOf(preferredStop, otherStop)
            fakePreferencesRepository.setSelectedStopCode(preferredStop.code)

            val result = getSelectedStopUseCase(null).first()

            assertEquals(preferredStop, result)
        }

    /**
     * test invoke returns stop from preferences if stop code is invalid but preferences exist
     */
    @Test
    fun test3() =
        runTest {
            val preferredStop = Stop(
                code = "PS",
                name = "Preferred Stop",
            )
            val otherStop = Stop(
                code = "JB",
                name = "Test Stop",
            )
            fakeGoTrainDataSource.stops = listOf(otherStop, preferredStop)
            fakePreferencesRepository.setSelectedStopCode(preferredStop.code)

            val result = getSelectedStopUseCase("invalid").first()

            assertEquals(preferredStop, result)
        }

    /**
     * test invoke returns default stop UN if stop code and preferences do not exist
     */
    @Test
    fun test4() = runTest {
        val defaultStop = Stop(
            code = "UN",
            name = "Union Stop",
        )
        val otherStop = Stop(
            code = "JB",
            name = "Test Stop",
        )
        fakeGoTrainDataSource.stops = listOf(otherStop, defaultStop)

        val result = getSelectedStopUseCase("NON_EXISTENT_CODE").first()

        assertEquals(defaultStop, result)
    }

    /**
     * test invoke returns the first stop in the list as a last resort
     */
    @Test
    fun test5() = runTest {
        fakeGoTrainDataSource.stops = List(5) {
            Stop(
                code = "S$it",
                name = "Stop $it",
            )
        }

        val result = getSelectedStopUseCase(null).first()

        assertEquals(Stop(code = "S0", name = "Stop 0"), result)
    }
}