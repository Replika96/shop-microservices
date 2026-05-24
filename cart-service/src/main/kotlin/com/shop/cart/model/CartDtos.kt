package com.shop.cart.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import java.math.BigDecimal

data class AddToCartRequest @JsonCreator constructor(
    @JsonProperty("productId") val productId: Long,
    @JsonProperty("quantity") @field:Min(1) val quantity: Int = 1
)

data class UpdateQuantityRequest @JsonCreator constructor(
    @JsonProperty("quantity") @field:Min(1) val quantity: Int
)

data class CartItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val price: BigDecimal,
    val quantity: Int,
    val imageUrl: String,
    val total: BigDecimal
)

data class CartResponse(
    val items: List<CartItemResponse>,
    val totalItems: Int,
    val totalPrice: BigDecimal
)

fun CartItem.toResponse() = CartItemResponse(
    id, productId, productName, price, quantity, imageUrl,
    price.multiply(BigDecimal(quantity))
)
