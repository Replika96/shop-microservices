package com.shop.notification.messaging

data class OrderEvent(
    val orderId: Long,
    val userEmail: String,
    val status: String,
    val productName: String
)
