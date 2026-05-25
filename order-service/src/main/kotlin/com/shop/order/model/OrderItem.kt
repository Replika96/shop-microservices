package com.shop.order.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "order_items")
class OrderItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order? = null,

    @Column(nullable = false) val productId: Long = 0,
    @Column(nullable = false) val productName: String = "",
    @Column(nullable = false) val price: BigDecimal = BigDecimal.ZERO,
    @Column(nullable = false) val quantity: Int = 1,
    val imageUrl: String = ""
)
