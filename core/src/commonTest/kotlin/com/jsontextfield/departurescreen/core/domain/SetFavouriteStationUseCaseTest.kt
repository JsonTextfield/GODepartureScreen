@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jsontextfield.departurescreen.core.domain

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

class SetFavouriteStationUseCaseTest {
    private lateinit var fakePreferencesRepository: FakePreferencesRepository
    private lateinit var setFavouriteStationUseCase: SetFavouriteStationUseCase

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        fakePreferencesRepository = FakePreferencesRepository()
        setFavouriteStationUseCase = SetFavouriteStationUseCase(
            preferencesRepository = fakePreferencesRepository,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test invoke adds station codes to favourites if not already present`() = runTest {
        val station = Station(
            code = "AB,CD",
            name = "Test Station",
        )
        fakePreferencesRepository.setFavouriteStations(setOf("EF"))

        setFavouriteStationUseCase(station)

        val expectedFavourites = setOf("EF", "AB", "CD")
        assertEquals(expectedFavourites, fakePreferencesRepository.getFavouriteStations().first())
    }

    @Test
    fun `test invoke removes station codes from favourites if already present`() = runTest {
        val station = Station(
            code = "AB,CD",
            name = "Test Station",
        )
        fakePreferencesRepository.setFavouriteStations(setOf("AB", "EF"))

        setFavouriteStationUseCase(station)

        val expectedFavourites = setOf("EF")
        assertEquals(expectedFavourites, fakePreferencesRepository.getFavouriteStations().first())
    }
}