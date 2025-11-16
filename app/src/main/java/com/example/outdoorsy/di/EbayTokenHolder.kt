package com.example.outdoorsy.di

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class EbayTokenHolder @Inject constructor() {
    var accessToken: String? = null
    var expirationTime: Long? = null

    fun isValid(): Boolean {
        if (accessToken == null || expirationTime == null) {
            return false
        }

        val buffer = 60.seconds
        return System.currentTimeMillis() < (expirationTime!! - buffer.inWholeMilliseconds)
    }

    fun updateToken(token: String, expiresInSeconds: Int) {
        accessToken = token
        expirationTime = System.currentTimeMillis() + (expiresInSeconds * 1000)
    }
}
