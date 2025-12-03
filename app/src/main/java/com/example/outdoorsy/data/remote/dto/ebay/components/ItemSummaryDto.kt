package com.example.outdoorsy.data.remote.dto.ebay.components

import com.example.outdoorsy.domain.model.ebay.EbayItem
import com.google.gson.annotations.SerializedName

data class ItemSummaryDto(
    val itemId: String,
    val title: String,
    val categories: List<CategoryDto>,
    val image: ImageDto,
    val price: PriceDto,
    val thumbnailImage: List<ImageDto>,
    @SerializedName("itemWebUrl")
    val url: String
)

fun ItemSummaryDto.toDomain(): EbayItem = EbayItem(
    this.itemId,
    title = this.title,
    price = this.price.toDomain(),
    imageUrl = this.image.url,
    link = this.url,
    categoryNames = this.categories.map { it.toDomain() }
)
