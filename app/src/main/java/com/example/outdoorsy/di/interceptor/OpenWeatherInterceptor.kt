package com.example.outdoorsy.di.interceptor

import com.example.outdoorsy.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that automatically appends the OpenWeather API key ("appid") as a query parameter
 * to every request.
 */
@Singleton
class OpenWeatherInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("appid", BuildConfig.OPENWEATHER_API_KEY)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}
