package com.example.outdoorsy.data.repository

import android.util.Log
import com.example.outdoorsy.data.remote.CurrencyApiService
import com.example.outdoorsy.domain.repository.CurrencyRepository
import javax.inject.Inject
import javax.inject.Singleton

// IMPORTANT: Replace with your actual API Key from currencyapi.com
private const val CURRENCY_API_KEY = "cur_live_KWm2zNtQci6fYOu5B5jIlbd60pvhlkwO4rPJiea9"

@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val currencyApiService: CurrencyApiService
) : CurrencyRepository {
    override suspend fun getConversionRate(baseCurrency: String, targetCurrency: String): Double? {
        if (baseCurrency == targetCurrency) return 1.0

        return try {
            val response = currencyApiService.getLatestRates(
                apiKey = CURRENCY_API_KEY,
                baseCurrency = baseCurrency,
                targetCurrencies = targetCurrency
            )
            val rate = response.data[targetCurrency]?.value
            if (rate == null) {
                Log.e("CurrencyRepo", "Target currency '$targetCurrency' not found in response.")
            }
            rate
        } catch (e: Exception) {
            Log.e("CurrencyRepo", "Failed to fetch conversion rate", e)
            null
        }
    }
}
