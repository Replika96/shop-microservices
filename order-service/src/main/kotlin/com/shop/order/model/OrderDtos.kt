package com.shop.order.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.time.LocalDateTime

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val page: Int,
    val size: Int
)

data class CreateOrderRequest(
    @field:NotBlank val address: String
)

data class UpdateStatusRequest @JsonCreator constructor(
    @JsonProperty("status") val status: OrderStatus
)

data class OrderItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val price: BigDecimal,
    val quantity: Int,
    val imageUrl: String,
    val total: BigDecimal
)

data class OrderResponse(
    val id: Long,
    val userEmail: String,
    val totalPrice: BigDecimal,
    val address: String,
    val status: OrderStatus,
    val createdAt: LocalDateTime,
    val items: List<OrderItemResponse>
)

fun OrderItem.toResponse() = OrderItemResponse(
    id, productId, productName, price, quantity, imageUrl,
    price.multiply(BigDecimal(quantity))
)

fun Order.toResponse() = OrderResponse(
    id, userEmail, totalPrice, address, status, createdAt,
    items.map { it.toResponse() }
)

// Внутренние DTO для парсинга ответа cart-service
data class CartItemDto(
    val id: Long = 0,
    val productId: Long = 0,
    val productName: String = "",
    val price: BigDecimal = BigDecimal.ZERO,
    val quantity: Int = 1,
    val imageUrl: String = "",
    val total: BigDecimal = BigDecimal.ZERO
)

data class CartResponseDto(
    val items: List<CartItemDto> = emptyList(),
    val totalItems: Int = 0,
    val totalPrice: BigDecimal = BigDecimal.ZERO
)

// RabbitMQ event
data class OrderEventItem(
    val productName: String = "",
    val quantity: Int = 0
)

data class OrderEvent(
    val orderId: Long = 0,
    val userEmail: String = "",
    val status: String = "",
    val items: List<OrderEventItem> = emptyList()
)
