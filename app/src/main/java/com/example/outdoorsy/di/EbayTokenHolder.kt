package com.example.outdoorsy.di

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

/**
 * A Singleton class responsible for holding and managing the eBay API access token in memory.
 * This is injected where the token is needed (e.g., in interceptors).
 */
@Singleton
class EbayTokenHolder @Inject constructor() {
    var accessToken: String? = null
    var expirationTime: Long? = null

    /**
     * Checks if the currently held token is valid and has not expired.
     * Includes a 60-second buffer to prevent using a token that is about to expire.
     */
    fun isValid(): Boolean {
        if (accessToken == null || expirationTime == null) {
            return false
        }

        val buffer = 60.seconds
        return System.currentTimeMillis() < (expirationTime!! - buffer.inWholeMilliseconds)
    }

    /**
     * Updates the stored access token and calculates the new expiration time.
     * @param token The new access token string.
     * @param expiresInSeconds The lifespan of the token in seconds.
     */
    fun updateToken(token: String, expiresInSeconds: Int) {
        accessToken = token
        expirationTime = System.currentTimeMillis() + (expiresInSeconds * 1000)
    }
}
