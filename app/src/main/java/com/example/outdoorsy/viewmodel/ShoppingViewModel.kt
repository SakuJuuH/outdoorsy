package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.domain.model.ebay.EbayItem
import com.example.outdoorsy.domain.model.ebay.Price
import com.example.outdoorsy.domain.repository.EbayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ShoppingViewModel @Inject constructor(private val ebayRepository: EbayRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchShoppingItems()
    }

    fun fetchShoppingItems() {
        // Define the searches you want to perform.
        val queries = listOf("hiking boots", "camping tent", "waterproof jacket", "backpack")

        Log.d("ShoppingViewModel", "Fetching items for queries: $queries")

        // 1. Set the loading state and clear old errors/items
        _uiState.update { it.copy(isLoading = true, error = null, items = emptyList()) }

        viewModelScope.launch {
            try {
                // 2. Run all queries in parallel for efficiency
                val results: List<EbayItem> = queries.map { query ->
                    async { ebayRepository.getItems(query) }
                }.awaitAll().flatten() // single list

                Log.d("ShoppingViewModel", "Successfully fetched ${results.size} items.")

                // 3. Update the state ONCE with the final list
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = results
                    )
                }
            } catch (e: Exception) {
                // 4. Or update ONCE on failure
                Log.e("ShoppingViewModel", "Error fetching items", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to load items. Please try again.")
                }
            }
        }
    }
}

data class ShoppingItem(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val imageUrl: String
)
