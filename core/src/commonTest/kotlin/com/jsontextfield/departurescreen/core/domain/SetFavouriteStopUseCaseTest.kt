@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jsontextfield.departurescreen.core.domain

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

class SetFavouriteStopUseCaseTest {
    private lateinit var fakePreferencesRepository: FakePreferencesRepository
    private lateinit var setFavouriteStopUseCase: SetFavouriteStopUseCase

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        fakePreferencesRepository = FakePreferencesRepository()
        setFavouriteStopUseCase = SetFavouriteStopUseCase(
            preferencesRepository = fakePreferencesRepository,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * test invoke adds stop codes to favourites if not already present
     */
    @Test
    fun test1() = runTest {
        val stop = Stop(
            code = "AB,CD",
            name = "Test Stop",
        )
        fakePreferencesRepository.setFavouriteStops(setOf("EF"))

        setFavouriteStopUseCase(stop)

        val expectedFavourites = setOf("EF", "AB", "CD")
        assertEquals(expectedFavourites, fakePreferencesRepository.getFavouriteStops().first())
    }

    /**
     * test invoke removes stop codes from favourites if already present
     */
    @Test
    fun test2() = runTest {
        val stop = Stop(
            code = "AB,CD",
            name = "Test Stop",
        )
        fakePreferencesRepository.setFavouriteStops(setOf("AB", "EF"))

        setFavouriteStopUseCase(stop)

        val expectedFavourites = setOf("EF")
        assertEquals(expectedFavourites, fakePreferencesRepository.getFavouriteStops().first())
    }
}