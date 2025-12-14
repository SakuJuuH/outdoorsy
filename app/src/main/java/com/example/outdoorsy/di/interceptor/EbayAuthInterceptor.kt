package com.example.outdoorsy.di.interceptor

import com.example.outdoorsy.di.EbayTokenHolder
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds the Bearer Token (Authorization header) to eBay API requests.
 * It retrieves the current valid token from [EbayTokenHolder].
 */
@Singleton
class EbayAuthInterceptor @Inject constructor(private val tokenHolder: EbayTokenHolder) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = tokenHolder.accessToken ?: return chain.proceed(originalRequest)

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}
