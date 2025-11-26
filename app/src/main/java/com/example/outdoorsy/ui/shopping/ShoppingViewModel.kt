package com.example.outdoorsy.ui.shopping

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.data.repository.SettingsRepository
import com.example.outdoorsy.domain.model.ebay.EbayItem
import com.example.outdoorsy.domain.model.ebay.Price
import com.example.outdoorsy.domain.repository.ActivityRepository
import com.example.outdoorsy.domain.repository.CurrencyRepository
import com.example.outdoorsy.domain.repository.EbayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DecimalFormat
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val ebayRepository: EbayRepository,
    private val currencyRepository: CurrencyRepository,
    private val settingsRepository: SettingsRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingUiState())
    val uiState = _uiState.asStateFlow()

    // We will keep the original, unconverted list in memory
    private var originalEbayItems: List<EbayItem> = emptyList()

    init {
        // 2. Start observing currency changes as soon as the ViewModel is created.
        observeCurrencyChanges()
        // 3. Perform the initial fetch of items from the network.
        fetchOriginalEbayItems()
    }

    private fun observeCurrencyChanges() {
        viewModelScope.launch {
            settingsRepository.getCurrency()
                .distinctUntilChanged()
                .debounce(300)
                .collectLatest { targetCurrency ->
                    Log.d(
                        "ShoppingViewModel",
                        "Currency changed to $targetCurrency. Re-converting prices."
                    )
                    if (originalEbayItems.isNotEmpty()) {
                        val convertedItems = convertItemPrices(originalEbayItems, targetCurrency)
                        _uiState.update { it.copy(items = convertedItems) }
                    }
                    // If originalEbayItems is empty, the initial fetch will handle conversion.
                }
        }
    }

    private fun fetchOriginalEbayItems() {
        val queries = listOf("hiking boots", "camping tent", "waterproof jacket", "backpack")
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // Fetch the raw items from eBay
                originalEbayItems = queries.map { query ->
                    async { ebayRepository.getItems(query) }
                }.awaitAll().flatten()

                Log.d(
                    "ShoppingViewModel",
                    "Successfully fetched ${originalEbayItems.size} original eBay items."
                )

                // Now get the current currency and perform the *first* conversion
                val targetCurrency = settingsRepository.getCurrency().first()
                val convertedItems = convertItemPrices(originalEbayItems, targetCurrency)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = convertedItems
                    )
                }
            } catch (e: Exception) {
                Log.e("ShoppingViewModel", "Error fetching original items", e)
                _uiState.update { it.copy(isLoading = false, error = "Failed to load items.") }
            }
        }
    }

    private suspend fun convertItemPrices(
        items: List<EbayItem>,
        targetCurrency: String
    ): List<EbayItem> {
        if (items.isEmpty()) return emptyList()
        val baseCurrency = items.first().price.currency
        val conversionRate = currencyRepository.getConversionRate(baseCurrency, targetCurrency)

        if (conversionRate == null || conversionRate == 1.0 || baseCurrency == targetCurrency) {
            Log.d("ShoppingViewModel", "Skipping conversion. Rate: $conversionRate")
            return items.map { it.copy(price = it.price.copy(currency = baseCurrency)) }
        }

        Log.d(
            "ShoppingViewModel",
            "Applying conversion rate of $conversionRate from $baseCurrency to $targetCurrency"
        )

        // Create the formatter instance. This is correct.
        val priceFormat = DecimalFormat("#,##0.00")

        return items.map { item ->
            // 1. Safely convert the price string to a Double. Use toDoubleOrNull for safety.
            val originalValue = item.price.value.toDoubleOrNull() ?: 0.0

            // 2. Perform the calculation with Doubles.
            val convertedValue = originalValue * conversionRate

            // 3. Format the final Double result into a String.
            val formattedValue = priceFormat.format(convertedValue)

            item.copy(
                price = Price(
                    value = formattedValue, // Use the formatted string
                    currency = targetCurrency
                )
            )
        }
    }
}
