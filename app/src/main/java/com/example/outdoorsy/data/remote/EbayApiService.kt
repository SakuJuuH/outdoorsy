package com.example.outdoorsy.data.remote

import com.example.outdoorsy.data.remote.dto.ebay.EbayItemResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface EbayApiService {

    @Headers("Accept: application/json")
    @GET("buy/browse/v1/item_summary/search")
    suspend fun getItems(
        @Header("X-EBAY-C-MARKETPLACE-ID") marketplaceId: String = "EBAY_GB",
        @Header("X-EBAY-C-ENDUSERCTX") context: String = "contextualLocation=country=FI",
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("filter") filter: String = "sellerAccountTypes:{BUSINESS},conditions:{NEW}"
    ): Response<EbayItemResponseDto>
}
