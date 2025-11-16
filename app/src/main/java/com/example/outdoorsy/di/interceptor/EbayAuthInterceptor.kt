package com.example.outdoorsy.di.interceptor

import com.example.outdoorsy.di.EbayTokenHolder
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

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
