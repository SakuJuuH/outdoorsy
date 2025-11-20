package com.example.outdoorsy.di.interceptor

import com.example.outdoorsy.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class EbayAccessAuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authKey = BuildConfig.EBAY_API_KEY

        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Basic $authKey")
            .build()

        return chain.proceed(newRequest)
    }
}
