package com.shop.order.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false) val userEmail: String,
    @Column(nullable = false) val productName: String,
    @Column(nullable = false) val quantity: Int,
    @Column(nullable = false) val price: BigDecimal,
    @Enumerated(EnumType.STRING) var status: OrderStatus = OrderStatus.PENDING,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class OrderStatus { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
