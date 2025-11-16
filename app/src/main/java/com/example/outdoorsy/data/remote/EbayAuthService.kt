package com.example.outdoorsy.data.remote

import com.example.outdoorsy.data.remote.dto.ebay.EbayTokenResponseDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface EbayAuthService {
    @FormUrlEncoded
    @POST("identity/v1/oauth2/token")
    suspend fun getToken(
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("scope") scope: String = "https://api.ebay.com/oauth/api_scope"
    ): EbayTokenResponseDto
}
