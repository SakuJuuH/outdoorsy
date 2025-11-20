package com.example.outdoorsy.viewmodel

import com.example.outdoorsy.domain.model.ebay.EbayItem

data class ShoppingUiState(
    val isLoading: Boolean = true,
    val items: List<EbayItem> = emptyList(),
    val error: String? = null,
    val query: String = ""
)
