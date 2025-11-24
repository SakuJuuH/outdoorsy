package com.example.outdoorsy.domain.repository

interface CurrencyRepository {
    suspend fun getConversionRate(baseCurrency: String, targetCurrency: String): Double?
}
