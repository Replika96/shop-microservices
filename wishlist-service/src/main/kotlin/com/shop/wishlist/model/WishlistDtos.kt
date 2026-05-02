package com.shop.wishlist.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class AddToWishlistRequest @JsonCreator constructor(
    @JsonProperty("productId") val productId: Long,
    @JsonProperty("productName") val productName: String,
    @JsonProperty("price") val price: BigDecimal,
    @JsonProperty("imageUrl") val imageUrl: String = ""
)

data class WishlistItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val price: BigDecimal,
    val imageUrl: String
)

fun WishlistItem.toResponse() = WishlistItemResponse(id, productId, productName, price, imageUrl)
