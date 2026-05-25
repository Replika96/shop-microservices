package com.shop.wishlist.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class AddToWishlistRequest @JsonCreator constructor(
    @JsonProperty("productId") val productId: Long
)

data class MergeWishlistRequest @JsonCreator constructor(
    @JsonProperty("guestId") val guestId: String
)

data class WishlistItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val price: BigDecimal,
    val imageUrl: String
)

fun WishlistItem.toResponse() = WishlistItemResponse(id, productId, productName, price, imageUrl)
