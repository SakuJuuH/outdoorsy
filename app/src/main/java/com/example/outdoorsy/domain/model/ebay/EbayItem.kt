package com.example.outdoorsy.domain.model.ebay

data class EbayItem(
    val title: String,
    val price: Price,
    val imageUrl: String,
    val link: String,
    val categoryNames: List<String>
)
