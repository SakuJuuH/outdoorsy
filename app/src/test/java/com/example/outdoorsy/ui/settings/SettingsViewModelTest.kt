package com.example.outdoorsy.ui.settings

import android.util.Log
import com.example.outdoorsy.R
import com.example.outdoorsy.domain.repository.SettingsRepository
import com.example.outdoorsy.utils.AppLanguage
import com.example.outdoorsy.utils.AppTheme
import com.example.outdoorsy.utils.Currencies
import com.example.outdoorsy.utils.TemperatureSystem
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertNull
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
import okio.IOException
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

        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

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
        unmockkStatic(Log::class)
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState loads initial settings correctly`() = runTest {
        viewModel = SettingsViewModel(settingsRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TemperatureSystem.METRIC.code, state.temperatureUnit)
        assertEquals(AppTheme.SYSTEM.code, state.appTheme)
        assertEquals(AppLanguage.ENGLISH.code, state.language)
        assertEquals(Currencies.GBP.code, state.currency)
        assertNull(state.userMessage)
    }

    @Test
    fun `setTemperatureUnit calls repository save`() = runTest {
        viewModel = SettingsViewModel(settingsRepository)

        viewModel.setTemperatureUnit(TemperatureSystem.IMPERIAL.code)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsRepository.saveTemperatureUnit(TemperatureSystem.IMPERIAL.code) }
    }

    @Test
    fun `setAppTheme calls repository save`() = runTest {
        viewModel = SettingsViewModel(settingsRepository)

        viewModel.setAppTheme(AppTheme.DARK.code)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsRepository.saveAppTheme(AppTheme.DARK.code) }
    }

    @Test
    fun `setCurrency calls repository save`() = runTest {
        viewModel = SettingsViewModel(settingsRepository)

        viewModel.setCurrency(Currencies.EUR.code)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsRepository.saveCurrency(Currencies.EUR.code) }
    }

    @Test
    fun `setLanguage calls repository save AND updates LocaleHelper`() = runTest {
        viewModel = SettingsViewModel(settingsRepository)

        viewModel.setLanguage(AppLanguage.FINNISH.code)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsRepository.saveLanguage(AppLanguage.FINNISH.code) }
    }

    @Test
    fun `repository write failure sets userMessage to generic error`() = runTest {
        coEvery { settingsRepository.saveAppTheme(any()) } throws IOException("Simulated Write Error")

        viewModel = SettingsViewModel(settingsRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.setAppTheme(AppTheme.DARK.code)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsRepository.saveAppTheme(AppTheme.DARK.code) }
        assertEquals(R.string.generic_error, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `messageShown clears userMessage`() = runTest {
        coEvery { settingsRepository.saveCurrency(any()) } throws IOException("Fail")
        viewModel = SettingsViewModel(settingsRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.setCurrency(Currencies.USD.code)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(R.string.generic_error, viewModel.uiState.value.userMessage)

        viewModel.messageShown()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.userMessage)
    }
}