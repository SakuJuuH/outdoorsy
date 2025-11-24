package com.example.outdoorsy.data.remote

import com.example.outdoorsy.data.remote.dto.currency.CurrencyResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {

    @GET("v3/latest")
    suspend fun getLatestRates(
        @Query("base_currency") baseCurrency: String,
        @Query("currencies") targetCurrencies: String
    ): CurrencyResponseDto
}
