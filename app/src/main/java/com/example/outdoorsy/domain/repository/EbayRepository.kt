package com.example.outdoorsy.domain.repository

import com.example.outdoorsy.domain.model.ebay.EbayItem

interface EbayRepository {
    suspend fun getItems(
        query: String,
        limit: Int = 5,
        filter: String = "sellerAccountTypes:{BUSINESS},conditions:{NEW}"
    ): List<EbayItem>
}
