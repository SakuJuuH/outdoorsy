package com.example.outdoorsy.data.repository

import android.util.Log
import com.example.outdoorsy.data.remote.EbayApiService
import com.example.outdoorsy.data.remote.EbayAuthService
import com.example.outdoorsy.data.remote.dto.ebay.toDomain
import com.example.outdoorsy.di.EbayApi
import com.example.outdoorsy.di.EbayAuth
import com.example.outdoorsy.di.EbayTokenHolder
import com.example.outdoorsy.domain.model.ebay.EbayItem
import com.example.outdoorsy.domain.repository.EbayRepository
import dagger.Lazy
import javax.inject.Inject

class EbayRepositoryImpl @Inject constructor(
    @param:EbayApi private val apiService: Lazy<EbayApiService>,
    @param:EbayAuth private val authService: Lazy<EbayAuthService>,
    private val tokenHolder: EbayTokenHolder
) : EbayRepository {
    override suspend fun getItems(query: String, limit: Int, filter: String): List<EbayItem> {
        if (!tokenHolder.isValid()) {
            getAccessToken()
        }

        val response = apiService.get().getItems(query = query, limit = limit, filter = filter)

        return if (response.isSuccessful && response.body() != null) {
            Log.d("EbayRepositoryImpl", "getItems: ${response.body()!!.toDomain()}")
            response.body()!!.toDomain()
        } else {
            emptyList()
        }
    }

    private suspend fun getAccessToken() {
        val response = authService.get().getToken()

        tokenHolder.updateToken(token = response.accessToken, expiresInSeconds = response.expiresIn)
    }
}
