package com.example.outdoorsy.data.remote.dto.ebay.components

import com.example.outdoorsy.domain.model.ebay.Price

data class PriceDto(val value: String, val currency: String) {
    fun toDomain(): Price = Price(value = value, currency = currency)
}
