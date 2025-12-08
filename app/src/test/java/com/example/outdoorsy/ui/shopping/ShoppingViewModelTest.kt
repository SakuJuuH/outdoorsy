package com.example.outdoorsy.ui.shopping

import android.util.Log
import com.example.outdoorsy.domain.repository.SettingsRepository
import com.example.outdoorsy.domain.model.ebay.EbayItem
import com.example.outdoorsy.domain.model.ebay.Price
import com.example.outdoorsy.domain.repository.ActivityRepository
import com.example.outdoorsy.domain.repository.CurrencyRepository
import com.example.outdoorsy.domain.repository.EbayRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingViewModelTest {

    @MockK
    private lateinit var ebayRepository: EbayRepository
    @MockK
    private lateinit var currencyRepository: CurrencyRepository
    @MockK
    private lateinit var settingsRepository: SettingsRepository
    @MockK
    private lateinit var activityRepository: ActivityRepository

    private lateinit var viewModel: ShoppingViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val sampleHikingBoots = EbayItem("1", "Hiking Boots", Price("100.0", "USD"), "", "", emptyList())

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0

        coEvery { currencyRepository.getConversionRate(any(), any()) } returns 1.0
        coEvery { ebayRepository.getItems(any(), any(), any()) } returns emptyList()
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        Dispatchers.resetMain()
    }

    private fun initializeViewModel() {
        viewModel = ShoppingViewModel(
            ebayRepository,
            currencyRepository,
            settingsRepository,
            activityRepository
        )
    }

    @Test
    fun `initial state is loading false and lists are empty`() {
        every { settingsRepository.getCurrency() } returns flowOf()
        initializeViewModel()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.items.isEmpty())
        assertTrue(state.recommendedItems.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `init sets error state when fetching fails`() = runTest {
        // ARRANGE
        val errorMessage = "Failed to load items."
        every { settingsRepository.getCurrency() } returns flowOf("USD")
        coEvery { activityRepository.getClothingItems() } returns emptyList()

        // This mock will cause the ViewModel's INTERNAL try-catch block to be entered.
        coEvery { ebayRepository.getItems(any(), any(), any()) } throws Exception("Network error")

        // ACT
        initializeViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // ASSERT
        // Now that the ViewModel handles the crash internally without propagating it,
        // the test can simply check the final UI state.
        val state = viewModel.uiState.value
        assertFalse("isLoading should be false after an error", state.isLoading)
        assertEquals("Error message was not set correctly in the catch block", errorMessage, state.error)
    }


    @Test
    fun `currency change reconverts prices without fetching new data`() = runTest {
        every { settingsRepository.getCurrency() } returns flowOf("USD", "EUR")
        coEvery { currencyRepository.getConversionRate("USD", "EUR") } returns 0.9

        initializeViewModel()

        val originalItemsField = viewModel::class.java.getDeclaredField("originalEbayItems")
        originalItemsField.isAccessible = true
        originalItemsField.set(viewModel, listOf(sampleHikingBoots))

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        val convertedBoot = state.items.find { it.itemId == "1" }
        assertEquals("EUR", convertedBoot?.price?.currency)
        // Verify no new network calls were made, since the data was already present.
        coVerify(exactly = 0) { ebayRepository.getItems(any(), any(), any()) }
    }
}
