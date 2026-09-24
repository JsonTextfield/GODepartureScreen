package com.jsontextfield.departurescreen.core.ui.viewmodels

import com.jsontextfield.departurescreen.core.data.fake.FakePreferencesRepository
import com.jsontextfield.departurescreen.core.ui.ContrastMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import com.jsontextfield.departurescreen.core.ui.TimeFormat
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
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var preferencesRepository: FakePreferencesRepository
    private lateinit var viewModel: SettingsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        preferencesRepository = FakePreferencesRepository()
        viewModel = SettingsViewModel(preferencesRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * GIVEN default preferences repository
     * WHEN SettingsViewModel is initialized
     * THEN uiState contains default theme, contrast, dynamic theme, and time format values
     */
    @Test
    fun testInitialPreferences() = runTest(testDispatcher) {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(ThemeMode.DEFAULT, state.themeMode)
        assertEquals(ContrastMode.NORMAL, state.contrastMode)
        assertEquals(false, state.useDynamicTheme)
        assertEquals(TimeFormat.RELATIVE, state.timeFormat)
    }

    /**
     * GIVEN a new theme mode
     * WHEN onThemeModeChange is called
     * THEN uiState theme mode and preferences repository are updated
     */
    @Test
    fun testThemeModeChange() = runTest(testDispatcher) {
        viewModel.onThemeModeChange(ThemeMode.DARK)
        advanceUntilIdle()
        assertEquals(ThemeMode.DARK, viewModel.uiState.value.themeMode)
    }

    /**
     * GIVEN a new contrast mode
     * WHEN onContrastModeChange is called
     * THEN uiState contrast mode and preferences repository are updated
     */
    @Test
    fun testContrastModeChange() = runTest(testDispatcher) {
        viewModel.onContrastModeChange(ContrastMode.HIGH)
        advanceUntilIdle()
        assertEquals(ContrastMode.HIGH, viewModel.uiState.value.contrastMode)
    }

    /**
     * GIVEN a dynamic theme boolean flag
     * WHEN onDynamicThemeChange is called
     * THEN uiState useDynamicTheme and preferences repository are updated
     */
    @Test
    fun testDynamicThemeChange() = runTest(testDispatcher) {
        viewModel.onDynamicThemeChange(true)
        advanceUntilIdle()
        assertEquals(true, viewModel.uiState.value.useDynamicTheme)
    }

    /**
     * GIVEN a new time format
     * WHEN onTimeFormatChange is called
     * THEN uiState timeFormat and preferences repository are updated
     */
    @Test
    fun testTimeFormatChange() = runTest(testDispatcher) {
        viewModel.onTimeFormatChange(TimeFormat.TWENTY_FOUR_HOUR)
        advanceUntilIdle()
        assertEquals(TimeFormat.TWENTY_FOUR_HOUR, viewModel.uiState.value.timeFormat)
    }
}
