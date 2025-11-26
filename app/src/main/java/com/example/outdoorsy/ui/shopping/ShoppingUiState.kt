package com.example.outdoorsy.ui.shopping

import com.example.outdoorsy.domain.model.ebay.EbayItem

data class ShoppingUiState(
    val isLoading: Boolean = false,
    val items: List<EbayItem> = emptyList(),
    val error: String? = null,
    val query: String = "",
    val recommendedItems: List<EbayItem> = emptyList()
)
