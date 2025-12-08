package com.example.outdoorsy.ui.shopping // Corrected package name

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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

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

    private var cachedClothingItems: List<String> = emptyList()

    init {
        observeDataAndCurrencyChanges()
    }

    private fun observeDataAndCurrencyChanges() {
        viewModelScope.launch {
            settingsRepository.getCurrency()
                .distinctUntilChanged()
                .debounce(300) // Debounce to avoid rapid updates
                .collectLatest { targetCurrency ->
                    // Check if the original data has been loaded yet.
                    if (originalEbayItems.isEmpty() && originalRecommendedItems.isEmpty()) {
                        // If not, this is the first run. Fetch everything.
                        Log.d(
                            "ShoppingViewModel",
                            "Initial data load. Target currency: $targetCurrency"
                        )
                        fetchAllShoppingData(targetCurrency)
                    } else {
                        // If data exists, just re-convert the prices with the new currency.
                        Log.d(
                            "ShoppingViewModel",
                            "Currency changed to $targetCurrency. Re-converting prices."
                        )
                        reconvertPrices(targetCurrency)
                    }
                }
        }
    }

    private suspend fun fetchAllShoppingData(targetCurrency: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        // Use supervisorScope to create a scope for concurrent tasks
        supervisorScope {
            try {
                // Now `async` is called within a proper CoroutineScope
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
                        emptyList()
                    } else {
                        clothingItems.map { query ->
                            async { ebayRepository.getItems(query) }
                        }.awaitAll().flatten()
                    }
                }

                originalEbayItems = mainItemsDeferred.await()
                originalRecommendedItems = recommendedItemsDeferred.await()

                Log.d(
                    "ShoppingViewModel",
                    "Fetched ${originalEbayItems.size} main items and ${originalRecommendedItems.size} recommended items."
                )

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

    private suspend fun reconvertPrices(targetCurrency: String) {
        val convertedItems = convertItemPrices(originalEbayItems, targetCurrency)
        val convertedRecommendedItems = convertItemPrices(originalRecommendedItems, targetCurrency)

        _uiState.update {
            it.copy(
                items = convertedItems,
                recommendedItems = convertedRecommendedItems
            )
        }
    }

    private suspend fun convertItemPrices(
        items: List<EbayItem>,
        targetCurrency: String
    ): List<EbayItem> {
        if (items.isEmpty()) return emptyList()

        val priceFormat = DecimalFormat("#,##0.00")

        return items.map { item ->
            val baseCurrency = item.price.currency
            if (baseCurrency == targetCurrency) return@map item

            val conversionRate = currencyRepository.getConversionRate(baseCurrency, targetCurrency)
            if (conversionRate == null) {
                Log.w(
                    "ShoppingViewModel",
                    "Skipping conversion for item '${item.title}'. No rate from $baseCurrency to $targetCurrency."
                )
                return@map item
            }

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

    fun refreshRecommendations() {
        viewModelScope.launch {
            val currentClothingItems = activityRepository.getClothingItems()

            Log.d("ShoppingViewModel", "Clothing items: $currentClothingItems")

            if (currentClothingItems.isNotEmpty() && currentClothingItems != cachedClothingItems) {
                Log.d("ShoppingViewModel", "Refreshing recommendations.")
                val targetCurrency = settingsRepository.getCurrency().first()

                try {
                    cachedClothingItems = currentClothingItems

                    val newRecommendedItems = coroutineScope {
                        currentClothingItems.map { query ->
                            async { ebayRepository.getItems(query) }
                        }.awaitAll().flatten()
                    }

                    Log.d(
                        "ShoppingViewModel",
                        "Fetched $newRecommendedItems new recommended items."
                    )

                    originalRecommendedItems = newRecommendedItems

                    val convertedRecommendedItems =
                        convertItemPrices(originalRecommendedItems, targetCurrency)
                    val convertedMainItems = convertItemPrices(originalEbayItems, targetCurrency)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = convertedMainItems,
                            recommendedItems = convertedRecommendedItems,
                            error = null
                        )
                    }
                } catch (e: Exception) {
                    Log.e("ShoppingViewModel", "Error refreshing recommendations", e)
                }
            }
        }
    }
}
