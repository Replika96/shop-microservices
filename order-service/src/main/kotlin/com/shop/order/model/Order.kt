package com.shop.order.model

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false) val userEmail: String = "",
    @Column(nullable = false) val totalPrice: BigDecimal = BigDecimal.ZERO,
    @Enumerated(EnumType.STRING) var status: OrderStatus = OrderStatus.PENDING,
    val address: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    val items: MutableList<OrderItem> = mutableListOf()
)

enum class OrderStatus { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
