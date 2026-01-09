@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jsontextfield.departurescreen.core.domain

import com.jsontextfield.departurescreen.core.data.fake.FakeGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.fake.FakePreferencesRepository
import com.jsontextfield.departurescreen.core.entities.Station
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

class GetSelectedStationUseCaseTest {

    private lateinit var fakeGoTrainDataSource: FakeGoTrainDataSource
    private lateinit var fakePreferencesRepository: FakePreferencesRepository
    private lateinit var getSelectedStationUseCase: GetSelectedStationUseCase

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        fakeGoTrainDataSource = FakeGoTrainDataSource()
        fakePreferencesRepository = FakePreferencesRepository()
        getSelectedStationUseCase = GetSelectedStationUseCase(
            preferencesRepository = fakePreferencesRepository,
            goTrainDataSource = fakeGoTrainDataSource,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * test invoke returns station for station code if it exists
     */
    @Test
    fun test1() = runTest {
        val station = Station(
            code = "JB",
            name = "Test Station",
        )
        fakeGoTrainDataSource.stations = listOf(station)

        val result = getSelectedStationUseCase(station.code).first()

        assertEquals(station, result)
    }

    /**
     * test invoke returns station from preferences if station code is null and preferences exist
     */
    @Test
    fun test2() =
        runTest {
            val preferredStation = Station(
                code = "PS",
                name = "Preferred Station",
            )
            val otherStation = Station(
                code = "JB",
                name = "Test Station",
            )
            fakeGoTrainDataSource.stations = listOf(preferredStation, otherStation)
            fakePreferencesRepository.setSelectedStationCode(preferredStation.code)

            val result = getSelectedStationUseCase(null).first()

            assertEquals(preferredStation, result)
        }

    /**
     * test invoke returns station from preferences if station code is invalid but preferences exist
     */
    @Test
    fun test3() =
        runTest {
            val preferredStation = Station(
                code = "PS",
                name = "Preferred Station",
            )
            val otherStation = Station(
                code = "JB",
                name = "Test Station",
            )
            fakeGoTrainDataSource.stations = listOf(otherStation, preferredStation)
            fakePreferencesRepository.setSelectedStationCode(preferredStation.code)

            val result = getSelectedStationUseCase("invalid").first()

            assertEquals(preferredStation, result)
        }

    /**
     * test invoke returns default station UN if station code and preferences do not exist
     */
    @Test
    fun test4() = runTest {
        val defaultStation = Station(
            code = "UN",
            name = "Union Station",
        )
        val otherStation = Station(
            code = "JB",
            name = "Test Station",
        )
        fakeGoTrainDataSource.stations = listOf(otherStation, defaultStation)

        val result = getSelectedStationUseCase("NON_EXISTENT_CODE").first()

        assertEquals(defaultStation, result)
    }

    /**
     * test invoke returns the first station in the list as a last resort
     */
    @Test
    fun test5() = runTest {
        fakeGoTrainDataSource.stations = List(5) {
            Station(
                code = "S$it",
                name = "Station $it",
            )
        }

        val result = getSelectedStationUseCase(null).first()

        assertEquals(Station(code = "S0", name = "Station 0"), result)
    }
}