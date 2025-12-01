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

    private var originalEbayItems: List<EbayItem> = emptyList()
    private var originalRecommendedItems: List<EbayItem> = emptyList()

    init {
        // Start observing currency changes immediately.
        // Data fetching will be triggered by the UI.
        observeCurrencyChanges()
    }

    // Fetches all data for the shopping page, including main items and recommendations.
    // This is made public to be called from the UI to refresh data on screen entry.
    fun fetchAllShoppingData() {
        // Set loading state only if we don't have any items yet.
        if (originalEbayItems.isEmpty() && originalRecommendedItems.isEmpty()) {
            _uiState.update { it.copy(isLoading = true, error = null) }
        }

        viewModelScope.launch {
            try {
                // Get the target currency first
                val targetCurrency = settingsRepository.getCurrency().first()

                // Launch both main items and recommended items fetches in parallel
                val mainItemsDeferred = async {
                    val queries =
                        listOf("hiking boots", "camping tent", "waterproof jacket", "backpack")
                    queries.map { query ->
                        async { ebayRepository.getItems(query) }
                    }.awaitAll().flatten()
                }

                val recommendedItemsDeferred = async {
                    val clothingItems = activityRepository.getClothingItems()
                    if (clothingItems.isEmpty()) {
                        Log.d("ShoppingViewModel", "No recommended clothing items found.")
                        emptyList() // Return an empty list if no clothing items
                    } else {
                        Log.d("ShoppingViewModel", "Fetching recommended items for: $clothingItems")
                        clothingItems.map { query ->
                            async { ebayRepository.getItems(query) }
                        }.awaitAll().flatten()
                    }
                }

                // Wait for both fetches to complete
                originalEbayItems = mainItemsDeferred.await()
                originalRecommendedItems = recommendedItemsDeferred.await()

                Log.d(
                    "ShoppingViewModel",
                    "Fetched ${originalEbayItems.size} main items and ${originalRecommendedItems.size} recommended items."
                )

                // Now, convert and update the UI state ONCE with all data
                val convertedItems = convertItemPrices(originalEbayItems, targetCurrency)
                val convertedRecommendedItems =
                    convertItemPrices(originalRecommendedItems, targetCurrency)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = convertedItems,
                        recommendedItems = convertedRecommendedItems,
                        error = null
                    )
                }
            } catch (e: Exception) {
                Log.e("ShoppingViewModel", "Error fetching shopping data", e)
                _uiState.update { it.copy(isLoading = false, error = "Failed to load items.") }
            }
        }
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
                    // Only convert if we have data to avoid running on initial load
                    if (originalEbayItems.isNotEmpty() || originalRecommendedItems.isNotEmpty()) {
                        val convertedItems = convertItemPrices(originalEbayItems, targetCurrency)
                        val convertedRecommendedItems =
                            convertItemPrices(originalRecommendedItems, targetCurrency)
                        _uiState.update {
                            it.copy(
                                items = convertedItems,
                                recommendedItems = convertedRecommendedItems
                            )
                        }
                    }
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

        val priceFormat = DecimalFormat("#,##0.00")

        return items.map { item ->
            val originalValue = item.price.value.toDoubleOrNull() ?: 0.0
            val convertedValue = originalValue * conversionRate
            val formattedValue = priceFormat.format(convertedValue)

            item.copy(
                price = Price(
                    value = formattedValue,
                    currency = targetCurrency
                )
            )
        }
    }
}
