package com.example.outdoorsy.data.remote.dto.ebay

import com.example.outdoorsy.data.remote.dto.ebay.components.ItemSummaryDto
import com.example.outdoorsy.data.remote.dto.ebay.components.toDomain
import com.example.outdoorsy.domain.model.ebay.EbayItem
import com.google.gson.annotations.SerializedName

data class EbayItemResponseDto(
    @SerializedName("itemSummaries")
    val itemSummaries: List<ItemSummaryDto>?
)

fun EbayItemResponseDto.toDomain(): List<EbayItem> = this.itemSummaries?.map {
    it.toDomain()
} ?: emptyList()
