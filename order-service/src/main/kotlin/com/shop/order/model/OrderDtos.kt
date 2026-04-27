package com.shop.order.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateOrderRequest(
    @field:NotBlank val productName: String,
    @field:Min(1) val quantity: Int,
    @field:Positive val price: BigDecimal
)

data class UpdateStatusRequest @JsonCreator constructor(
    @JsonProperty("status") val status: OrderStatus
)

data class OrderResponse(
    val id: Long, val userEmail: String, val productName: String,
    val quantity: Int, val price: BigDecimal,
    val status: OrderStatus, val createdAt: LocalDateTime
)

fun Order.toResponse() = OrderResponse(id, userEmail, productName, quantity, price, status, createdAt)

data class OrderEvent(val orderId: Long, val userEmail: String, val status: String, val productName: String)
