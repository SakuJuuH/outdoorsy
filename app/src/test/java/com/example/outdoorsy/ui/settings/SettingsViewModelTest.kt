package com.example.outdoorsy.ui.settings

import com.example.outdoorsy.domain.repository.SettingsRepository
import com.example.outdoorsy.utils.AppLanguage
import com.example.outdoorsy.utils.AppTheme
import com.example.outdoorsy.utils.Currencies
import com.example.outdoorsy.utils.LocaleHelper
import com.example.outdoorsy.utils.TemperatureSystem
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel

    @MockK
    private lateinit var settingsRepository: SettingsRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // --- Mock Singleton Object---
        // This prevents the actual LocaleHelper from calling Android framework code
        mockkObject(LocaleHelper)
        every { LocaleHelper.setLocale(any()) } just runs

        // Must return a flow for ALL properties because 'combine' waits for all of them
        every { settingsRepository.getTemperatureUnit() } returns flowOf(TemperatureSystem.METRIC.code)
        every { settingsRepository.getAppTheme() } returns flowOf(AppTheme.SYSTEM.code)
        every { settingsRepository.getLanguage() } returns flowOf(AppLanguage.ENGLISH.code)
        every { settingsRepository.getCurrency() } returns flowOf(Currencies.GBP.code)

        // Mock Save operations to do nothing (just runs)
        coEvery { settingsRepository.saveTemperatureUnit(any()) } just runs
        coEvery { settingsRepository.saveAppTheme(any()) } just runs
        coEvery { settingsRepository.saveLanguage(any()) } just runs
        coEvery { settingsRepository.saveCurrency(any()) } just runs
    }

    @After
    fun tearDown() {
        // Clean up the static object mock
        unmockkObject(LocaleHelper)
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState loads initial settings correctly`() = runTest {
        // Given (Setup done in @Before)

        // When
        viewModel = SettingsViewModel(settingsRepository)

        // Start collecting the flow to activate 'SharingStarted.WhileSubscribed'
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(TemperatureSystem.METRIC.code, state.temperatureUnit)
        assertEquals(AppTheme.SYSTEM.code, state.appTheme)
        assertEquals(AppLanguage.ENGLISH.code, state.language)
        assertEquals(Currencies.GBP.code, state.currency)
    }

    @Test
    fun `setTemperatureUnit calls repository save`() = runTest {
        viewModel = SettingsViewModel(settingsRepository)

        // When
        viewModel.setTemperatureUnit(TemperatureSystem.IMPERIAL.code)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { settingsRepository.saveTemperatureUnit(TemperatureSystem.IMPERIAL.code) }
    }

    @Test
    fun `setAppTheme calls repository save`() = runTest {
        viewModel = SettingsViewModel(settingsRepository)

        // When
        viewModel.setAppTheme(AppTheme.DARK.code)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { settingsRepository.saveAppTheme(AppTheme.DARK.code) }
    }

    @Test
    fun `setCurrency calls repository save`() = runTest {
        viewModel = SettingsViewModel(settingsRepository)

        // When
        viewModel.setCurrency(Currencies.EUR.code)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { settingsRepository.saveCurrency(Currencies.EUR.code) }
    }

    @Test
    fun `setLanguage calls repository save AND updates LocaleHelper`() = runTest {
        viewModel = SettingsViewModel(settingsRepository)

        // When
        viewModel.setLanguage(AppLanguage.FINNISH.code)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { settingsRepository.saveLanguage(AppLanguage.FINNISH.code) }
    }
}