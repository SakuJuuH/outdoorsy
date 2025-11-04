package com.example.outdoorsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ShoppingItem(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val imageUrl: String // For a placeholder image
)

class ShoppingViewModel : ViewModel() {

    // StateFlow for "Recommended" items
    private val _recommendedItems = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val recommendedItems: StateFlow<List<ShoppingItem>> = _recommendedItems.asStateFlow()

    // StateFlow for "All" items
    private val _allItems = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val allItems: StateFlow<List<ShoppingItem>> = _allItems.asStateFlow()

    init {
        fetchMockData()
    }

    private fun fetchMockData() {
        viewModelScope.launch {
            val allMockItems = listOf(
                ShoppingItem(1, "Waterproof Hiking Boots", 129.99, "Keep your feet dry on any trail.", "Footwear", ""),
                ShoppingItem(2, "Insulated Down Jacket", 199.50, "Lightweight but warm for chilly evenings.", "Apparel", ""),
                ShoppingItem(3, "Solar-Powered Lantern", 39.95, "Eco-friendly lighting for your campsite.", "Accessories", ""),
                ShoppingItem(4, "3-Person Camping Tent", 249.00, "Easy-setup tent, perfect for weekend trips.", "Equipment", ""),
                ShoppingItem(5, "Portable Water Filter", 25.00, "Clean drinking water wherever you go.", "Essentials", ""),
                ShoppingItem(6, "All-Weather Backpack", 89.99, "Durable and spacious for all your gear.", "Bags", ""),
                ShoppingItem(7, "Merino Wool Socks", 22.00, "Comfortable and moisture-wicking.", "Apparel", ""),
                ShoppingItem(8, "Headlamp", 45.50, "Hands-free lighting for night hikes.", "Accessories", ""),
                ShoppingItem(9, "Camping Cookset", 75.00, "All-in-one set for campfire meals.", "Equipment", ""),
                ShoppingItem(10, "Trekking Poles", 65.00, "Reduce strain on your knees during hikes.", "Essentials", "")
            )

            // For demonstration, let's say the first 3 items are "recommended"
            _recommendedItems.value = allMockItems.take(2)
            _allItems.value = allMockItems
        }
    }

    fun onAddToCart(item: ShoppingItem) {
        println("Added ${item.name} to cart.")
    }
}
