package com.example.outdoorsy.data.repository

import android.util.Log
import com.example.outdoorsy.data.local.dao.CurrencyRateDao
import com.example.outdoorsy.data.local.entity.CurrencyRateEntity
import com.example.outdoorsy.data.remote.CurrencyApiService
import com.example.outdoorsy.di.module.IoDispatcher
import com.example.outdoorsy.domain.repository.CurrencyRepository
import com.example.outdoorsy.utils.Currencies
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val currencyApiService: CurrencyApiService,
    private val currencyRateDao: CurrencyRateDao,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
) : CurrencyRepository {

    private val apiBaseCurrency = "USD"
    private val cacheExpirationTimeMillis = TimeUnit.HOURS.toMillis(6)

    private val refreshMutex = Mutex()

    /**
     * Retrieves the conversion rate between a base and target currency, utilizing a local cache
     * to avoid unnecessary network calls.
     *
     * @param baseCurrency The currency to convert from (e.g., "USD").
     * @param targetCurrency The currency to convert to (e.g., "EUR").
     * @return The conversion rate as a [Double], or null if the rate cannot be determined.
     */
    override suspend fun getConversionRate(baseCurrency: String, targetCurrency: String): Double? =
        withContext(dispatcher) {
            if (baseCurrency == targetCurrency) return@withContext 1.0

            // Attempt to find the most recent rate entry in the database.
            // We only need one entry to check the timestamp.
            refreshMutex.withLock {
                val lastRate = currencyRateDao.getMostRecentRate()
                val now = System.currentTimeMillis()

                val isCacheStale =
                    lastRate == null || (now - lastRate.timestamp) > cacheExpirationTimeMillis

                if (isCacheStale) {
                    Log.d("CurrencyRepo", "Cache is stale or empty. Fetching fresh rates from API.")
                    fetchAndCacheAllRatesFromApi()
                } else {
                    Log.d(
                        "CurrencyRepo",
                        "Cache is fresh. Using local data for $baseCurrency -> $targetCurrency."
                    )
                }
            }

            // After ensuring the cache is fresh, calculate the rate.
            return@withContext calculateRateFromCache(baseCurrency, targetCurrency)
        }

    /**
     * Fetches all available currency rates from the API relative to [apiBaseCurrency],
     * clears the old cache, and saves the new rates to the database.
     */
    private suspend fun fetchAndCacheAllRatesFromApi() {
        try {
            val targetCurrencies = Currencies.entries
                .map { it.code }
                .filter { it != apiBaseCurrency }
                .joinToString(",")

            // Fetch latest rates from the API, relative to our defined API base (USD).
            val response = currencyApiService.getLatestRates(
                baseCurrency = apiBaseCurrency,
                targetCurrencies = targetCurrencies
            )
            val ratesFromApi = response.data
            val newTimestamp = System.currentTimeMillis()

            val ratesToStore = ratesFromApi.map { (currencyCode, rateData) ->
                CurrencyRateEntity(
                    baseCurrency = apiBaseCurrency,
                    targetCurrency = currencyCode,
                    rate = rateData.value,
                    timestamp = newTimestamp
                )
            }

            if (ratesToStore.isNotEmpty()) {
                // Clear old data and insert fresh data in a single transaction for safety.
                currencyRateDao.clearAndInsert(ratesToStore)
                Log.i(
                    "CurrencyRepo",
                    "Successfully cached ${ratesToStore.size} new currency rates."
                )
            } else {
                Log.w("CurrencyRepo", "API returned no currency rates.")
            }
        } catch (e: Exception) {
            Log.e("CurrencyRepo", "Failed to fetch and cache currency rates", e)
        }
    }

    /**
     * Calculates the conversion rate between two currencies using the cached data.
     * It uses the [apiBaseCurrency] as a pivot for calculations (e.g., to get EUR -> CAD,
     * it calculates (USD -> CAD) / (USD -> EUR)).
     *
     * @param baseCurrency The currency to convert from.
     * @param targetCurrency The currency to convert to.
     * @return The calculated rate, or null if a rate is missing.
     */
    private suspend fun calculateRateFromCache(
        baseCurrency: String,
        targetCurrency: String
    ): Double? {
        // Find the rate for the target currency relative to our API base (e.g., USD -> EUR)
        val targetRateAgainstApiBase = if (targetCurrency == apiBaseCurrency) {
            1.0
        } else {
            currencyRateDao.getRate(apiBaseCurrency, targetCurrency)?.rate
        }

        // Find the rate for the base currency relative to our API base (e.g., USD -> CAD)
        val baseRateAgainstApiBase = if (baseCurrency == apiBaseCurrency) {
            1.0
        } else {
            currencyRateDao.getRate(apiBaseCurrency, baseCurrency)?.rate
        }

        // Ensure both rates were found and the base rate is not zero to avoid division errors.
        return if (targetRateAgainstApiBase != null && baseRateAgainstApiBase != null &&
            baseRateAgainstApiBase != 0.0
        ) {
            // The final rate is the ratio of the target rate to the base rate.
            // (e.g., rate for CAD -> EUR = (USD -> EUR) / (USD -> CAD))
            targetRateAgainstApiBase / baseRateAgainstApiBase
        } else {
            Log.e(
                "CurrencyRepo",
                "Could not calculate rate for $baseCurrency -> $targetCurrency. One of the currencies might be missing from the cache."
            )
            null
        }
    }
}
