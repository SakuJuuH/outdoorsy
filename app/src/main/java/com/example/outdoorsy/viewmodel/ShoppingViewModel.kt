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
        fetchMockData()
        testRepository()
    }

    private fun testRepository() {
        val testQuery = listOf("hiking boots", "fleece jacket") // Use any query you want

        Log.d("RepoTest", "--- STARTING REPOSITORY TEST with query: '$testQuery' ---")

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // 2. Run all queries in parallel
                val results: List<List<EbayItem>> = testQuery.map { query ->
                    async { ebayRepository.getItems(query) } // This throws an exception on failure
                }.awaitAll() // Wait for all of them to finish

                // 3. Update the state ONCE with the final, flattened list
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = results.flatten() // Combines all lists into one
                    )
                }
                Log.d("RepoTest", "$results")
                Log.d("RepoTest", "--- FINISHED REPOSITORY TEST ---")
            } catch (e: Exception) {
                // 4. Or update ONCE on failure
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun fetchMockData() {
        viewModelScope.launch {
            val allMockItems = listOf(
                ShoppingItem(
                    1,
                    "Waterproof Hiking Boots",
                    129.99,
                    "Keep your feet dry on any trail.",
                    "Footwear",
                    ""
                ),
                ShoppingItem(
                    2,
                    "Insulated Down Jacket",
                    199.50,
                    "Lightweight but warm for chilly evenings.",
                    "Apparel",
                    ""
                ),
                ShoppingItem(
                    3,
                    "Solar-Powered Lantern",
                    39.95,
                    "Eco-friendly lighting for your campsite.",
                    "Accessories",
                    ""
                ),
                ShoppingItem(
                    4,
                    "3-Person Camping Tent",
                    249.00,
                    "Easy-setup tent, perfect for weekend trips.",
                    "Equipment",
                    ""
                ),
                ShoppingItem(
                    5,
                    "Portable Water Filter",
                    25.00,
                    "Clean drinking water wherever you go.",
                    "Essentials",
                    ""
                ),
                ShoppingItem(
                    6,
                    "All-Weather Backpack",
                    89.99,
                    "Durable and spacious for all your gear.",
                    "Bags",
                    ""
                ),
                ShoppingItem(
                    7,
                    "Merino Wool Socks",
                    22.00,
                    "Comfortable and moisture-wicking.",
                    "Apparel",
                    ""
                ),
                ShoppingItem(
                    8,
                    "Headlamp",
                    45.50,
                    "Hands-free lighting for night hikes.",
                    "Accessories",
                    ""
                ),
                ShoppingItem(
                    9,
                    "Camping Cookset",
                    75.00,
                    "All-in-one set for campfire meals.",
                    "Equipment",
                    ""
                ),
                ShoppingItem(
                    10,
                    "Trekking Poles",
                    65.00,
                    "Reduce strain on your knees during hikes.",
                    "Essentials",
                    ""
                )
            )

            _uiState.update {
                it.copy(
                    items = allMockItems.map { item ->
                        EbayItem(
                            title = item.name,
                            price = Price(
                                value = item.price.toString(),
                                currency = "Eur"
                            ),
                            imageUrl = item.imageUrl,
                            link = "",
                            categoryNames = emptyList()
                        )
                    }
                )
            }
        }
    }

    fun onAddToCart(item: EbayItem) {
        println("Added ${item.title} to cart.")
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
